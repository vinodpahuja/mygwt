package rnd.mywt.client.mvc.page.board;

import rnd.mywt.client.mvc.MVCBean;

public interface Board extends MVCBean {

	public enum BoardType {
		FORM_BOARD, DATA_BOARD;
	}

	BoardType getBoardType();

	String MODULE_NAME = "moduleName";

	String getModuleName();

	String APPLICATION_BEAN_NAME = "appBeanName";

	String getApplicationBeanName();

}
