package com.storage.roomValidate.controllers;


import com.storage.roomValidate.models.Coordinates;
import com.storage.roomValidate.models.Point;
import com.storage.roomValidate.repo.PointRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RoomController {

    @Autowired
    private PointRepository pointRepository;

    public static Coordinates coordinates = new Coordinates();

    @GetMapping("/validateRoom")
    public String roomValidate(Model model) {

        createRoom();

        model.addAttribute("coord", coordinates.getPoints());
        return "room-validate";
    }

    @RequestMapping(value = "/validateRoom", method = RequestMethod.POST)
    public String addCoordinate(@RequestParam("xcoor") String x, @RequestParam("ycoor") String y, Model model) {

        try {
            int xcoor = Integer.parseInt(x);
            int ycoor = Integer.parseInt(y);

            Point point = new Point(xcoor, ycoor);
            coordinates.getPoints().add(point);
            pointRepository.save(point);

            model.addAttribute("coord", coordinates.getPoints());
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Your entered values that is not integer!");
        } finally {
            return "room-validate";
        }
    }

    @RequestMapping(value = "/drawing", method = RequestMethod.POST)
    public String drawAndValidate(Model model) {
        model.addAttribute("coord", coordinates.getPoints());
        model.addAttribute("message", validate());
        return "drawing";
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    public String clear(Model model) {

        coordinates.getPoints().clear();
        pointRepository.deleteAll();
        return "redirect:/validateRoom";
    }

    @PostMapping("/response")
    @ResponseBody
    public String postResponseController(@RequestBody String room) throws JSONException {
        String jsonString = new String(room);
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arrOfResults = obj.getJSONArray("room");
        String xcoor = new String();
        String ycoor = new String();

        for (int i = 0; i < arrOfResults.length(); i++) {
            try {
                xcoor = arrOfResults.getJSONObject(i).getString("x");
                ycoor = arrOfResults.getJSONObject(i).getString("y");
                pointRepository.save(new Point(Integer.parseInt(xcoor), Integer.parseInt(ycoor)));
            } catch (NumberFormatException e) {
                return "USE ONLY INTEGER!";
            }
        }

        createRoom();
        String message = validate();
        return message;
    }

    public void createRoom() {
        if (coordinates.getPoints().size() == 0) {
            Iterable<Point> points = pointRepository.findAll();
            for (Point p : points) {
                coordinates.getPoints().add(new Point(p.getX(), p.getY()));
            }
        }
    }

    public String validate() {

        if (coordinates.getPoints().size() < 4) {
            return "Your room is bad! Count of coordinates are less than 4";
        }

        for (Point p : coordinates.getPoints()) {
            if (p.getX() < 0 || p.getY() < 0)
                return "Your room is bad! Coordinates can not be minus!";

        }

        for (int i = 1; i < coordinates.getPoints().size(); i++) {
            if (coordinates.getPoints().get(i).getX() != coordinates.getPoints().get(i - 1).getX() &&
                    coordinates.getPoints().get(i).getY() != coordinates.getPoints().get(i - 1).getY())
                return "Your room is bad! You entered a diagonal wall!";
        }

        if (coordinates.getPoints().get(coordinates.getPoints().size() - 1).getX() != 0 ||
                coordinates.getPoints().get(coordinates.getPoints().size() - 1).getY() != 0) {
            return "Your room is bad! It doesn't have a finite area!";
        }

        return "Your room is good!";
    }

}
