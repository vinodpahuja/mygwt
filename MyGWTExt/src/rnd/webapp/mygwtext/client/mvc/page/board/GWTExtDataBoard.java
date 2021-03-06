package rnd.webapp.mygwtext.client.mvc.page.board;

import java.io.Serializable;
import java.util.List;

import rnd.bean.ApplicationDynaBean;
import rnd.bean.ValueChangeEvent;
import rnd.bean.ValueChangeListener;
import rnd.bean.ValueChangeListenerAdapter;
import rnd.bean._Bean;
import rnd.expression.Expression;
import rnd.expression.XChangeEvent;
import rnd.expression.XChangeListener;
import rnd.mywt.client.MyWTHelper;
import rnd.mywt.client.arb.ARBServiceResponseHandler;
import rnd.mywt.client.data.ColumnMetaData;
import rnd.mywt.client.data.DataTable;
import rnd.mywt.client.data.FilterInfo;
import rnd.mywt.client.data._Row;
import rnd.mywt.client.data.RowCache;
import rnd.mywt.client.data.RowMetaData;
import rnd.mywt.client.expression.BeanPropertyExpression;
import rnd.mywt.client.expression.BindingManager;
import rnd.mywt.client.expression.ContextValueExpression;
import rnd.mywt.client.mvc.field.Field;
import rnd.mywt.client.mvc.field.Table;
import rnd.mywt.client.mvc.field.Table.RowTableModel;
import rnd.mywt.client.mvc.field.data.ReferenceField;
import rnd.mywt.client.mvc.page.board.DataBoard;
import rnd.mywt.client.rpc.util.ARUtils;
import rnd.utils.WrapperUtils;

import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Panel;

public class GWTExtDataBoard extends GWTExtBoard implements DataBoard {

	private Panel contextPanel;

	private Table table;
	private ReferenceField referenceField;

	public GWTExtDataBoard(String moduleName, String appBeanName, String viewName) {
		super(moduleName, appBeanName);
		setValue(VIEW_NAME, viewName);
		setModel(new GWTExtDataBoardModel());
	}

	public BoardType getBoardType() {
		return BoardType.DATA_BOARD;
	}

	public ReferenceField getReferenceField() {
		return this.referenceField;
	}

	public void setReferenceField(ReferenceField referenceField) {
		this.referenceField = referenceField;
	}

	public Table getTable() {
		if (this.table == null) {
			this.table = createTable();
		}
		return this.table;
	}

	public void refreshTable() {
		if (((DataBoardModel) getModel()).isDataTableMetaDataIntialized()) {
			fetchDataTable(false);
		}
	}

	private Panel getContextPanel() {

		if (contextPanel == null) {
			contextPanel = new Panel();
			contextPanel.setBorder(false);
		}
		return contextPanel;

	}

	@Override
	public Expression addContextField(String fieldName, Field field) {
		addChild(field);
		getContextPanel().add((Widget) field.getView().getViewObject());
		ContextValueExpression ctxExp = new ContextValueExpression(fieldName);
		BindingManager.bindExpression(field, new BeanPropertyExpression(field.getFieldProperty()), this.getModel(), ctxExp);
		return ctxExp;
	}

	@Override
	protected Panel createPanel() {

		Panel dataBoardPanel = super.createPanel();
		dataBoardPanel.setTitle(getViewName());
		dataBoardPanel.setBorder(false);

		dataBoardPanel.add(getContextPanel());

		dataBoardPanel.add((Panel) getTable().getView().getViewObject());

		return dataBoardPanel;
	}

	private Table createTable() {
		Table newTable = MyWTHelper.getMVCFactory().createTable(Table.ROW_BASED);
		newTable.setParent(this);
		fetchDataTable(true);
		return newTable;
	}

	private void fetchDataTable(final boolean refreshMetaData) {
		// Logger.startMethod("GWTExtDataBoard", "fetchDataTable");
		FilterInfo filterInfo = getFilter();
		if (filterInfo != null) {
			filterInfo.calculateFilterParams();
		}

		// final RuntimeException re = new RuntimeException("outer");

		// Logger.log("getFilter()", getFilter());
		((DataBoardModel) getModel()).setDataTableIntialized(false);

		MyWTHelper.getARB().executeRequest(ARUtils.createFetchRequest(getModuleName(), getApplicationBeanName(), getViewName(), filterInfo), new ARBServiceResponseHandler() {

			@Override
			public void processResult(Serializable result) {
				// re.printStackTrace();
				// new RuntimeException("inner").printStackTrace();

				try {
					DataTable dataTable = (DataTable) result;

					if (refreshMetaData) {
						((DataBoardModel) getModel()).setDataTableMetaDataIntialized(false);
						ColumnMetaData[] columnMetaData = dataTable.getRowMetaData().getColumnMetaDatas();
						table.setColumnMetaDatas(columnMetaData);
						((DataBoardModel) getModel()).setDataTableMetaDataIntialized(true);
					}

					((Table.RowTableModel) table.getModel()).setDataTable(dataTable);
					((DataBoardModel) getModel()).setDataTableIntialized(true);

					List<_Row> rows = dataTable.getRows();
					if (rows.size() > 0) {
						RowMetaData rmd = rows.get(0).getRowMetaData();
						for (_Row row : rows) {
							RowCache.get().addRow(rmd.getModuleName(), rmd.getApplicationBeanName(), rmd.getViewName(), row);
						}
					}

				} catch (RuntimeException e) {
					e.printStackTrace();
					throw e;
				}
			}
		});

		// Logger.endMethod("GWTExtDataBoard", "fetchDataTable");
	}

	public void addRow(_Row newRow) {
		((RowTableModel) getTable().getModel()).addRow(newRow);
	}

	public void removeCurrentRow() {
		((RowTableModel) getTable().getModel()).removeCurrentRow();
	}

	public void updateCurrentRow(_Row updatedRow) {
		// Logger.startMethod("GWTExtDataBoard", "updateCurrentRow");
		((RowTableModel) getTable().getModel()).updateCurrentRow(updatedRow);
		// Logger.endMethod("GWTExtDataBoard", "updateCurrentRow");
	}

	public FilterInfo getFilter() {
		return (FilterInfo) getValue(FILTER);
	}

	public void setFilter(FilterInfo filterInfo) {
		// Logger.startMethod("GWTExtDataBoard", "setFilter");
		// Logger.log("filterInfo", filterInfo);
		setValue(FILTER, filterInfo);
		// Logger.endMethod("GWTExtDataBoard", "setFilter");
	}

	@Override
	public String getViewName() {
		return (String) getValue(VIEW_NAME);
	}

	public class GWTExtDataBoardModel implements DataBoardModel {

		private DataBoardRefresher dataBoardRefresher = new DataBoardRefresher();

		private class DataBoardRefresher implements XChangeListener {

			public void stateChanged(XChangeEvent changeEvent) {
				refreshTable();
			}

		}

		public boolean isDataTableIntialized() {
			return WrapperUtils.getBoolean(getValue(DATA_TABLE_INTIALIZED));
		}

		public void setDataTableIntialized(boolean dataTableIntialized) {
			setValue(DATA_TABLE_INTIALIZED, dataTableIntialized);
		}

		public boolean isDataTableMetaDataIntialized() {
			return WrapperUtils.getBoolean(getValue(DATA_TABLE_META_DATA_INTIALIZED));
		}

		public void setDataTableMetaDataIntialized(boolean dataTableMetaDataIntialized) {
			setValue(DATA_TABLE_META_DATA_INTIALIZED, dataTableMetaDataIntialized);
		}

		public boolean isFilterReset() {
			return WrapperUtils.getBoolean(getValue(FILTER_RESET));
		}

		public void setFilterReset(boolean filterReset) {
			setValue(FILTER_RESET, filterReset);
		}

		private ApplicationDynaBean context = new ApplicationDynaBean();

		public Object getContext(String key) {
			return context.getValue(key);
		}

		public Object setContext(String key, Object value) {
			return context.setValue(key, value);
		}

		@Override
		public _Bean getContextBean() {
			return context;
		}

		@Override
		public void addValueChangeListener(String propertyName, ValueChangeListener vcl) {
			context.addValueChangeListener(propertyName, vcl);
		}

		@Override
		public void removeValueChangeListener(String propertyName, ValueChangeListener vcl) {
			context.removeValueChangeListener(propertyName, vcl);
		}

		public GWTExtDataBoardModel() {

			addValueChangeListener(DATA_TABLE_META_DATA_INTIALIZED, new ValueChangeListenerAdapter<Boolean>() {
				public void valueChanged(ValueChangeEvent<Boolean> vce) {
					if (isDataTableMetaDataIntialized()) {
						if (isFilterReset()) {
							refreshTable();
							setFilterReset(false);
						}
					}
				}
			});

			addValueChangeListener(DATA_TABLE_INTIALIZED, new ValueChangeListenerAdapter<Boolean>() {
				public void valueChanged(ValueChangeEvent<Boolean> vce) {
					if (isDataTableIntialized()) {
						if (referenceField != null) {
							if (referenceField.getText() != null && referenceField.getText().trim().length() != 0) {
								Object search = referenceField.search(referenceField.getText());
								if (search == null) {
									referenceField.setValue(null);
								}
							}
						}
					}
				}
			});

			addValueChangeListener(FILTER, new ValueChangeListenerAdapter<FilterInfo>() {

				public void valueChanged(ValueChangeEvent<FilterInfo> vce) {
					setFilterReset(true);
					refreshTable();

					FilterInfo oldFilterInfo = vce.getOldValue();
					if (oldFilterInfo != null) {

						Object[] filterParamExpressionObjects = oldFilterInfo.getFilterParamExpressionObjects();
						Expression[] filterParamExpressions = oldFilterInfo.getFilterParamExpressions();

						for (int i = 0; i < filterParamExpressions.length; i++) {
							Expression expression = filterParamExpressions[i];
							expression.removeXChangeListener(filterParamExpressionObjects[i], dataBoardRefresher);
						}
					}

					FilterInfo newFilterInfo = vce.getNewValue();
					if (newFilterInfo != null) {

						Object[] filterParamExpressionObjects = newFilterInfo.getFilterParamExpressionObjects();
						Expression[] filterParamExpressions = newFilterInfo.getFilterParamExpressions();

						for (int i = 0; i < filterParamExpressions.length; i++) {
							Expression expression = filterParamExpressions[i];
							expression.addXChangeListener(filterParamExpressionObjects[i], dataBoardRefresher);
						}
					}

				}

			});
		}
	}

}