package com.rieke.bmore.catan.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getUser(
            //@RequestParam(value = "state") String state,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user",new User(1,"tcrieke","Tyler","Rieke","867-5309"));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }
}
