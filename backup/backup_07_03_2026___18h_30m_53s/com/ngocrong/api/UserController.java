package com.ngocrong.api;

import com.ngocrong.data.PlayerData;
import com.ngocrong.data.UserData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.SQLStatement;
import com.ngocrong.server.Server;
import com.ngocrong.server.SessionManager;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import com.ngocrong.user.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import _HunrProvision.HoangAnhDz;

@RestController
@RequestMapping(path = "/api/server")
public class UserController {

    @Autowired
    private static final Logger logger = Logger.getLogger(ServerController.class);

    @PostMapping("/cms")
    public ResponseEntity<?> registerUser(@RequestBody UserData userData) {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(SQLStatement.REGISTER);
            ps.setString(1, userData.getUsername());
            ps.setString(2, userData.getPassword());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.status(HttpStatus.OK).body("Register success");
            }
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Register failed");
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
}
