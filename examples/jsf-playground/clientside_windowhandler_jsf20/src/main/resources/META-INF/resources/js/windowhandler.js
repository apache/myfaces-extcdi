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

function isHtml5() {
    try {
        return !!localStorage.getItem;
    } catch(e) {
        return false;
    }
}

// some browsers don't understand JSON - guess which one ... :(
function stringify(someArray) {
    if (JSON) {
        return JSON.stringify(someArray);
    }
    return someArray.join("|||");
}

// store the current body in the html5 localstorage
function storeWindowTree()
{
    // first we store all CSS we also need on the intermediate page
    var headNodes = document.getElementsByTagName("head")[0].childNodes;
    var oldSS = new Array();
    var j=0;
    for (i=0; i<headNodes.length; i++) {
        var tagName = headNodes[i].tagName;
        if (tagName && equalsIgnoreCase(tagName, "link") &&
            equalsIgnoreCase(headNodes[i].getAttribute("type"), "text/css")) {

            // sort out media="print" and stuff
            var media = headNodes[i].getAttribute("media");
            if (!media || equalsIgnoreCase(media, "all") || equalsIgnoreCase(media, 'screen')) {
                oldSS[j++] = headNodes[i].getAttribute("href");
            }
        }
    }

    localStorage.setItem(window.name + '_css', stringify(oldSS));
    var body = document.getElementsByTagName("body")[0];

    localStorage.setItem(window.name + '_body', body.innerHTML);

    //X TODO: store ALL attributes of the body tag
    localStorage.setItem(window.name + '_bodyAttrs', body.getAttribute("class"));

    // store x and y scroll positions
    localStorage.setItem(window.name + '_x', window.pageXOffset);
    localStorage.setItem(window.name + '_y', window.pageYOffset);

    return true;
}

function equalsIgnoreCase(source, destination) {
    //either both are not set or null
    if (!source && !destination) {
        return true;
    }
    //source or dest is set while the other is not
    if (!source || !destination) return false;

    //in any other case we do a strong string comparison
    return source.toLowerCase() === destination.toLowerCase();
}

function applyOnClick()
{
    var links = document.getElementsByTagName("a");
    for(var i = 0; i < links.length; i++)
    {
        if (!links[i].onclick)
        {
            links[i].setAttribute('onclick', 'storeWindowTree(); return true;');
        }
        else
        {
            var oldClick = links[i].getAttribute('onclick').replace(/\'/g,'\\\'');
            var onclickCode = "jsf.util.chain(this, event, storeWindowTree(), '" + oldClick + "');";
            links[i].setAttribute('onclick', onclickCode);
        }
    }
}

function assertWindowId() {
    var freshWindow = window.name.length < 1;
    if (freshWindow) {
        url = urlWithoutWindowId(window.location.href);
        window.name = "window";
        window.location = url;
    }
}


window.onload = function()
{
    if (isHtml5())
    {
        applyOnClick();
    }

    // this ensures that even without the ClientSideWindowHandler
    assertWindowId();
}
