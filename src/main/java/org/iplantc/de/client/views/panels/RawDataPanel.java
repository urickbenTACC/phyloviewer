package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.FileEditorWindowDirtyEvent;
import org.iplantc.de.client.events.FileEditorWindowSavedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.services.RawDataServices;
import org.iplantc.de.client.views.dialogs.IPlantDialog;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//private variables
	private String idWorkspace;
	private String data;	
	private TextArea areaData;
	private String textOrig = new String();	
	private boolean editable;
	private MessageBox wait;
	
	///////////////////////////////////////
	//constructor
	public RawDataPanel(String idWorkspace,FileIdentifier file,String data,boolean editable)
	{
		super(file);		
	
		this.idWorkspace = idWorkspace;
		this.data = data;
		this.editable = editable;
		
		EventBus eventbus = EventBus.getInstance();
		wait = MessageBox.wait("Progress", displayStrings.fileSaveProgress(), "Saving...");
		wait.close();
		
		eventbus.addHandler(FileSaveAsEvent.TYPE,new FileSaveAsEventHandler() 
		{
			@Override
			public void onSaved(FileSaveAsEvent event) 
			{
				wait.close();				
			}			
		});

		buildTextArea();			
	}
	
	///////////////////////////////////////
	//private methods
	private void buildTextArea()
	{		
		areaData = buildTextArea(editable);
		
		//we don't need to listen for changes if we are not editable
		if(editable)
  	  	{
			areaData.addListener(Events.OnKeyUp, new Listener<FieldEvent>() 
			{
				public void handleEvent(FieldEvent be) 
				{
					String text = areaData.getValue();
  
					if(!text.equals(textOrig))
					{
						textOrig = text;
	  
						//don't fire event if we are already dirty
						if(!dirty)
						{		   
							dirty = true;
							EventBus eventbus = EventBus.getInstance();							
							FileEditorWindowDirtyEvent event = new FileEditorWindowDirtyEvent(file.getFileId());
							eventbus.fireEvent(event);
						}
					}
				}			      
			});
  	  	}
	}
	
	///////////////////////////////////////
	private void doSave()
	{
		if(areaData != null)
		{
			String body = areaData.getValue();	
			
			if(file != null)
			{	
				//toolbar.setEnabled(false);
				wait.show();
				RawDataServices.saveRawData(file.getFileId(),file.getFilename(),body,new AsyncCallback<String>()
				{
					@Override
					public void onSuccess(String result) 
					{
						EventBus eventbus = EventBus.getInstance();							
						FileEditorWindowSavedEvent event = new FileEditorWindowSavedEvent(file.getFileId());
						eventbus.fireEvent(event);	
						wait.close();
						Info.display("Save", displayStrings.fileSave());
					}					
					
					@Override
					public void onFailure(Throwable caught) 
					{
						DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);
						ErrorHandler.post(errorStrings.rawDataSaveFailed());
						wait.close();
					}					
				});
			}
		}
	}
	
	///////////////////////////////////////
	private void promptSaveAs()
	{		 
		IPlantDialog dlg = new IPlantDialog(displayStrings.saveAs(),320,new RawDataSaveAsDialogPanel(idWorkspace,file,areaData.getValue(),wait));
		dlg.show();
	}
	
	///////////////////////////////////////
	private ToolBar buildToolbar()
	{
		ToolBar ret = new ToolBar();
		final int TOOLBAR_HEIGHT = 24;
		
		ret.setWidth(getWidth());
		ret.setHeight(TOOLBAR_HEIGHT);
		
		ret.add(new Button(displayStrings.save(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doSave();				
			}			
		}));		
		
		//add our Save As button
		ret.add(new Button(displayStrings.saveAs(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				promptSaveAs();				
			}			
		}));
		
		return ret;		
	}
		
	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		if(data != null)
		{			
			textOrig = data;
			areaData.setValue(data);
			areaData.setWidth(getWidth());
			
			ContentPanel panel = new ContentPanel();
			panel.setHeaderVisible(false);
			panel.setLayout(new FitLayout());
			panel.setWidth(getWidth());
			panel.add(areaData);			
			
			if(editable)
			{
				panel.setTopComponent(buildToolbar());
			}
			
			add(panel,centerData);
		}
	}	
	
	///////////////////////////////////////
	@Override
	protected void afterRender() 
	{
		super.afterRender();
		areaData.el().setElementAttribute("spellcheck","false");
	}

	///////////////////////////////////////
	//public methods
	@Override
	public String getTabHeader() 
	{
		return displayStrings.raw();
	}	
		
	///////////////////////////////////////
	public int getTabIndex()
	{
		return 0;
	}
}