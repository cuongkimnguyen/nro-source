package com.ngocrong.api;

import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.server.ServerMaintenance;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.user.User;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import _HunrProvision.HoangAnhDz;

@RestController
@RequestMapping(path = "/api/server")
public class ServerController {

    private static final Logger logger = Logger.getLogger(ServerController.class);

    @GetMapping(path = "/bao-tri")
    public ResponseEntity<Integer> maintenance(@RequestParam("min") Integer min, @RequestParam("message") String message) {
        int rs = -1;
        try {
            Server server = DragonBall.getInstance().getServer();
            if (!server.isMaintained) {
                ServerMaintenance serverMaintenance = new ServerMaintenance(message, min * 60);
                Thread t = new Thread(serverMaintenance);
                t.start();
                rs = 1;
            } else {
                rs = 0;
            }
        } catch (Exception ex) {
            
            logger.error("maintenance", ex);
        }
        return new ResponseEntity<>(rs, HttpStatus.OK);
    }

    @GetMapping(path = "/get-item")
    public ResponseEntity<sendItem> getItemData(
            @RequestParam("playerId") int playerId,
            @RequestParam("tempId") int tempId,
            @RequestParam("optionItem") String options) {
        try {
            // Parse the options string into a 2D array
            int[][] itemOptions = parseOptions(options);

            // Construct response
            sendItem itemData = new sendItem(playerId, tempId, itemOptions);
            return new ResponseEntity<>(itemData, HttpStatus.OK);
        } catch (Exception ex) {
            
            logger.error("getItemData", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private int[][] parseOptions(String options) throws IllegalArgumentException {
        try {
            String[] rows = options.split(";");
            int[][] parsedOptions = new int[rows.length][2];

            for (int i = 0; i < rows.length; i++) {
                String[] values = rows[i].split(",");
                if (values.length != 2) {
                    throw new IllegalArgumentException("Invalid format for options: " + options);
                }
                parsedOptions[i][0] = Integer.parseInt(values[0]);
                parsedOptions[i][1] = Integer.parseInt(values[1]);
            }

            return parsedOptions;
        } catch (NumberFormatException e) {
            
            throw new IllegalArgumentException("Invalid number format in options: " + options, e);
        }
    }

    public static class sendItem {

        int playerId;
        private int tempId;
        private ArrayList<ItemOption> itemoption;

        public sendItem(int playerId, int tempId, int[][] itemOptions) {
            itemoption = new ArrayList<>();
            this.playerId = playerId;
            this.tempId = tempId;
            for (int[] itemOption : itemOptions) {
                itemoption.add(new ItemOption(itemOption[0], itemOption[1]));
            }
            sendItem();
        }

        void sendItem() {
            Item item = new Item(tempId);
            item.options = itemoption;

            Player _player = SessionManager.findChar(playerId);
            if (_player != null) {
                _player.addItem(item);
                _player.service.sendThongBao("Bạn nhận được :" + item.template.name);
            }
        }
    }
}
