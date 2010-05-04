package rnd.mywt.client.application;

import rnd.mywt.client.application.FormHelper;
import rnd.mywt.client.expression.RowColumnExpression;
import rnd.mywt.client.mvc.MVCHandlerFactory;
import rnd.mywt.client.mvc.field.Table;
import rnd.mywt.client.mvc.field.data.ReferenceField;
import rnd.mywt.client.mvc.field.data.ReferenceField.ReferenceFieldView;
import rnd.mywt.client.mvc.field.data.text.TextArea;
import rnd.mywt.client.mvc.field.data.text.TextField;
import rnd.mywt.client.mvc.page.form.Form;

public abstract class AbstractFormHelper implements FormHelper {

	private Form form;

	public Form getForm() {
		if (this.form == null) {
			this.form = createForm();
		}
		return this.form;
	}

	public Form createForm() {
		Form newForm = MVCHandlerFactory.getMVCHandler().createForm();
		// newForm.setName(getFormName());
		return newForm;
	}

	protected TextField createTextField(String label, String boundTo) {
		TextField textField = MVCHandlerFactory.getMVCHandler().createTextField(label);
		textField.setBoundTo(boundTo);
		return textField;
	}
	
	protected TextArea createTextArea(String label, String boundTo) {
		TextArea textArea = MVCHandlerFactory.getMVCHandler().createTextArea(label);
		textArea.setBoundTo(boundTo);
		return textArea;
	}


	protected ReferenceField createReferenceField(String label, String moduleName, String applicationBeanName, String viewName) {
		return MVCHandlerFactory.getMVCHandler().createReferenceField(label, moduleName, applicationBeanName, viewName);
	}

	protected ReferenceField createReferenceField(String label, String boundTo, String moduleName, String applicationBeanName, String viewName, String columnName) {
		ReferenceField referenceField = createReferenceField(label, moduleName, applicationBeanName, viewName);
		configureReferenceField(referenceField, boundTo, columnName);
		return referenceField;
	}

	protected Table createTable() {
		return MVCHandlerFactory.getMVCHandler().createTable(Table.BEAN_BASED);
	}

	protected void configureReferenceField(ReferenceField referenceField, String boundTo, String columnName) {
		referenceField.setBoundTo(boundTo);
		((ReferenceFieldView) referenceField.getView()).setDisplayExpresion(new RowColumnExpression(columnName));
	}

}