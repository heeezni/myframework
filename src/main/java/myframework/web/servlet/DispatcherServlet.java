package myframework.web.servlet;

import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 웹 애플리케이션의 모든 요청을 1차적으로 처리하는 전면 컨트롤러 (관제탑)
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

	/**
	 * 이 서블릿이 초기화 될 때, 매핑 파일에 등록된 컨트롤러들만 인스턴스를 생성하여 모아야 하는데, 이 서블릿이 직접하지 않고, 개발자가
	 * 등록한 핸들러 매핑에게 맡김 + 추후 요청 처리할 때도, 어떤 하위 컨트롤러가 동작해야 하는지도 핸들러 매핑이 알아서 분석하여 이
	 * 서블릿에게 반환함
	 * 
	 * 왜? 어제까지는 요청이 들어올 때마다 하위 컨트롤러의 인스턴스를 생성하는 방식이기 때문에 메모리 낭비
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		// 초기화 파라미터 읽기 (설정파일의 위치 얻기)
		String contextConfigLocation = config.getInitParameter("contextConfigLocation");
		// 현재 애플리케이션의 정보 얻기
		ServletContext context = config.getServletContext();
		String realPath = context.getRealPath(contextConfigLocation);

		try (FileReader reader = new FileReader(realPath)) {
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject(); // GSON에 있는 JSON 파싱메서드
			// root만 있으면 JSON을 휘어잡을 수 있다!
			log.debug("root=" + root);

			String mappingType = root.get("mappingType").getAsString();
			log.debug("우리가 사용할 핸들러 매핑은 " + mappingType);

		} catch (Exception e) {

		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response);

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

}
