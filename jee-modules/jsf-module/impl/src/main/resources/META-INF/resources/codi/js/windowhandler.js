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
/**
 *
 * Add MyFaces CODI windowhandler cookie to all unused hrefs
 */
 //reserve namespace for codi
if(!myfaces) {
    var myfaces = {};
}
myfaces.codi = myfaces.codi || {};

myfaces.codi.getFullUrl = function(url) {
    var urlparts = url.split(/&|\?/g);
    var urlpath = urlparts[0];
    if (urlpath.indexOf('://') < 0) {
        if (urlpath[0]== '/') {
            return urlpath;
        }
        else {
            var startpathloc = window.location.pathname.lastIndexOf('/');
            return window.location.pathname.substring(0, startpathloc+1) + urlpath;
        }
    }

    return url;
}

myfaces.codi.addWindowCookie = function(url) {
    var windowId = window.name;
    if (windowId == undefined) {
        return;
    }

    // 1 second expiry time
    var expdt = new Date();
    expdt.setTime(expdt.getTime()+(1*1000));
    var expires = "; expires="+expdt.toGMTString();
    var fullurl = myfaces.codi.getFullUrl(url);
    var pathName = encodeURIComponent(fullurl.replace(/\//g, ""));
    document.cookie = pathName + '-codiWindowId=' + windowId + expires;
}

myfaces.codi._decorateOnClick = function(element, url) {
    var oldOnClick = element.onclick;
    var newOnClick = function() {
       // addWindowCookie(url); return true;
       myfaces.codi.addWindowCookie(url);
       return true;
    }
    //now to the complicated part normally onclick delivers an event object
    //but in case of ie the event is a window.event object, so we cannot rely
    //on the object being delivered
    element.onclick =  function(event) {
        var ret = newOnClick.apply(this, arguments);
        if(ret && oldOnClick) {
            return oldOnClick.apply(this, arguments);
        }
        return ret;
    };
}


//now we store our handlers in the namespace for codi
myfaces.codi._initOnLoad = function(){
    var onLoadHandler =  function() {
        var anchors = document.getElementsByTagName("a");
        var i=0;
        for (i=0;i < anchors.length;i++) {
            var a = anchors[i];
            var url = a.getAttribute('href');
            myfaces.codi._decorateOnClick(a, url);
        }
    }
    var oldOnLoadHandler = window.onload;
    window.onload = function() {
        onLoadHandler();
        if(oldOnLoadHandler) {
            oldOnLoadHandler();
        }
    }
}


//we now trigger the setup
myfaces.codi._initOnLoad();

