package rnd.mywt.server.bean;

import rnd.bean.ApplicationBean;

public class ApplicationJavaBean extends JavaBean implements ApplicationBean {

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// Due to no-access to applicationId by JavaBean
	// If we do not override it JavaBean have to cycle through whole Class-Tree
	// @Override
	// protected Class<?> getFieldType(String propertyName) throws NoSuchFieldException {
	// if (propertyName.equals("applicationBeanId")) { return Long.class; }
	// return super.getFieldType(propertyName);
	// }

	@Override
	public String toString() {
		return super.toString() + "-" + getId();
	}

}
