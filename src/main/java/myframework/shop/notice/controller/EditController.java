package myframework.shop.notice.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myframework.web.servlet.Controller;

public class EditController implements Controller{

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
	}

	@Override
	public String getViewName() {
		return "/shop/notice/edit/view";
	}

	@Override
	public boolean isForward() {
		return false;
	}

}
