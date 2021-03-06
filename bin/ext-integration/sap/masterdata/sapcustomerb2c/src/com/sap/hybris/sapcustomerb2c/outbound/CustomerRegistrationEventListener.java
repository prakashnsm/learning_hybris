/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Catch the register event and start the <code>sapCustomerPublishProcess</code> business process
 */
public class CustomerRegistrationEventListener extends AbstractEventListener<RegisterEvent>
{
	private ModelService modelService;

	private BaseStoreService baseStoreService;

	private SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService;

	/**
	 * @return businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
	}

	/**
	 * @return modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * start the <code>sapCustomerPublishProcess</code> business process
	 * 
	 */
	@Override
	protected void onEvent(final RegisterEvent registerEvent)
	{
		// only if replication of user is requested start publishing to Data Hub process
		if (getSapCoreSAPGlobalConfigurationService().getProperty("replicateregistereduser"))
		{

			final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = (StoreFrontCustomerProcessModel) getBusinessProcessService()
					.createProcess("customerPublishProcess" + System.currentTimeMillis(), "customerPublishProcess");
			storeFrontCustomerProcessModel.setSite(registerEvent.getSite());
			storeFrontCustomerProcessModel.setCustomer(registerEvent.getCustomer());
			//			storeFrontCustomerProcessModel.set
			final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
			if (currentBaseStore != null)
			{
				storeFrontCustomerProcessModel.setStore(currentBaseStore);
			}

			getModelService().save(storeFrontCustomerProcessModel);
			getBusinessProcessService().startProcess(storeFrontCustomerProcessModel);
		}
	}

	/**
	 * return a SAPGlobalConfigurationServiceImpl instance
	 * 
	 * @return sapCoreSAPGlobalConfigurationService
	 */
	public SAPGlobalConfigurationServiceImpl getSapCoreSAPGlobalConfigurationService()
	{
		return sapCoreSAPGlobalConfigurationService;
	}

	/**
	 * set the SAPGlobalConfigurationServiceImpl instance
	 * 
	 * @param sapCoreSAPGlobalConfigurationService
	 */
	public void setSapCoreSAPGlobalConfigurationService(
			final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService)
	{
		this.sapCoreSAPGlobalConfigurationService = sapCoreSAPGlobalConfigurationService;
	}

	/**
	 * return Base store service instance
	 * 
	 * @return baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * Set Base Store Service instance
	 * 
	 * @param baseStoreService
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
