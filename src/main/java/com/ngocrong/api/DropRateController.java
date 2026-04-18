package com.ngocrong.api;

import com.ngocrong.server.DropRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/drop-rate")
public class DropRateController {
//
//    @GetMapping
//    public Map<String, Integer> getRate() {
//        Map<String, Integer> res = new HashMap<>();
//        res.put("mob", DropRateService.getMobRate());
//        res.put("boss", DropRateService.getBossRate());
//        return res;
//    }
//
//    @PostMapping
//    public ResponseEntity<?> update(@RequestParam("mob") int mob,
//            @RequestParam("boss") int boss,
//            @RequestParam("tilemob") int boss,
//            @RequestParam("") int boss) {
//        DropRateService.update(mob, boss);
//        return ResponseEntity.ok().build();
//    }
}
