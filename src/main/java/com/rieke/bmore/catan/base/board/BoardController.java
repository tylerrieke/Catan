package com.rieke.bmore.catan.base.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcrie on 7/25/2017.
 */
@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BasicBoardFactory boardFactory;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getBoard(
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("board", boardFactory.create().getTiles());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }
}
