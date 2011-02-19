/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.examples.codi.jsf12.message.client;

import org.apache.myfaces.examples.codi.jsf12.message.client.model.Client;
import org.apache.myfaces.examples.codi.jsf12.message.client.model.ClientId;
import org.apache.myfaces.extensions.validator.util.JsfUtils;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.faces.event.ValueChangeEvent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Named
@SessionScoped
public class ClientManager implements Serializable
{
    private static final long serialVersionUID = -5818582475786894173L;

    private ClientId currentClientId = ClientId.CLIENT_A;

    private List<SelectItem> clients = new ArrayList<SelectItem>();

    @PostConstruct
    protected void init()
    {
        for (ClientId value : ClientId.values())
        {
            this.clients.add(new SelectItem(
                    value, JsfUtils.getMessageFromApplicationMessageBundle("lbl_ClientId." + value.getClientId())));
        }
    }

    @Produces
    @RequestScoped
    public Client getCurrentClient()
    {
        //TODO load client from service (by id)
        return new Client(this.currentClientId, "test client");
    }

    public void clientChanged(ValueChangeEvent valueChangeEvent)
    {
        this.currentClientId = (ClientId)valueChangeEvent.getNewValue();
        FacesContext.getCurrentInstance().renderResponse();
    }

    /*
     * generated
     */
    public List<SelectItem> getClients()
    {
        return clients;
    }

    public ClientId getCurrentClientId()
    {
        return currentClientId;
    }

    public void setCurrentClientId(ClientId currentClientId)
    {
        this.currentClientId = currentClientId;
    }
}
