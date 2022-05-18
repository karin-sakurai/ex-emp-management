package jp.co.sample.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sample.domain.Administrator;
import jp.co.sample.form.InsertAdministratorForm;
import jp.co.sample.form.LoginForm;
import jp.co.sample.service.AdministratorService;

@Controller
@RequestMapping("/")
public class AdministratorController {

	@Autowired
	private AdministratorService Service;

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public InsertAdministratorForm setUpInsertAdministratorForm() {
		return new InsertAdministratorForm();
	}

	@ModelAttribute
	public LoginForm setUpLoginForm() {
		return new LoginForm();
	}

	@RequestMapping("/toInsert")
	public String toInsert() {
		return "administrator/insert";
	}

	@RequestMapping("/insert")
	public String insert(@Validated InsertAdministratorForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return toInsert();
		}

		Administrator administrator1 = Service.findmail(form.getMailAddress());

		if (administrator1 != null) {
			String mailerror = "メールアドレスが重複しています";
			model.addAttribute("mailerror", mailerror);
			return "administrator/insert";
		} else {
		Administrator administrator = new Administrator();
		BeanUtils.copyProperties(form, administrator);
		Service.insert(administrator);
		return "redirect:/";
	}
	}

	@RequestMapping("/")
	public String toLogin() {
		return "administrator/login";
	}

	@RequestMapping("/login")
	public String login(@Validated LoginForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return toLogin();
		}

		Administrator administrator = Service.login(form.getMailAddress(), form.getPassword());

		if (administrator == null) {
			String message = "メールアドレスまたはパスワードが不正です。";
			model.addAttribute("message", message);
			return "administrator/login";
		} else {
			session.setAttribute("administratorName", administrator.getName());
			return "forward:/employee/showList";
		}

	}

	@RequestMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}

}

