package wangqiang.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import wangqiang.website.service.MusicRepository;

/**
 * Created by wangq on 2017/5/17.
 */
@Controller
public class index {

    @Autowired
    private MusicRepository repository;

    @GetMapping("/")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("ids",repository.findMaxCommont());
        return modelAndView;
    }
}
