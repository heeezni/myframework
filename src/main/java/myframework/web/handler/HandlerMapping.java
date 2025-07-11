package myframework.web.handler;

import com.google.gson.JsonObject;

/**
 * 모든 핸들러 매핑 객체들의 최상위 객체를 정의하여 구현을 강제하자!
 * DispatcherServlet이 의존함 
 * */
public interface HandlerMapping {
	
    /**
     * 루트 JSON 설정을 주입받아 핸들러 매핑에 필요한 설정정보(JSON)를 참조하게 함.
     * DispatcherServlet이 보유한 Root JsonObject가 있어야 Json설정파일 해석가능하므로 넘겨받자
     * 예: admin-servlet.json
     */
	public void setRoot(JsonObject root); 
	
	
	// 각 핸들러 매핑 후, 하고싶은 초기화 작업에 사용할 메서드
	public void initialize();

    /**
     * 전달받은 URI에 따라 적절한 Controller 객체 반환.
     * DispatcherServlet에서 호출됨.
     */	public Controller getController(String uri);

}

