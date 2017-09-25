package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute  @Valid Menu menu,
                                         Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String displayAddItem(@PathVariable int menuId, Model model) {
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm addForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", addForm);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(@ModelAttribute  @Valid AddMenuItemForm addForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("form", addForm);
            model.addAttribute("title", "Add item to menu: " + addForm.getMenu().getName());
            return "menu/add-item";
        }
        Menu menu = menuDao.findOne(addForm.getMenuId());
        Cheese cheese = cheeseDao.findOne(addForm.getCheeseId());
        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }
}
