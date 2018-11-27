/**
 *  ****************  Welcome Home Parent App  ****************
 *
 *  Design Usage:
 *  This app is designed to give a personal welcome announcement after you have entered the home.
 *
 *  Copyright 2018 Bryan Turcotte (@bptworld)
 *
 *  Special thanks to (@Cobra) for use of his Parent/Child code and various other bits and pieces.
 *  
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 *
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *
 *
 *
 *  V1.0.0 - 11/25/18 - Initial release.
 *
 */

definition(
    name:"Welcome Home",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "Parent App for 'Welcome Home' childapps ",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
    )

preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
} 

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childApps.size()} child apps"
    childApps.each {child ->
    log.info "Child app: ${child.label}"
    }
    
}

def mainPage() {
    dynamicPage(name: "mainPage") {
    	installCheck()
        
		if(state.appInstalled == 'COMPLETE'){
			display()
				section() {
					paragraph "This app is designed to give a personal welcome announcement after you have entered the home."
				}
				section("Instructions:", hideable: true, hidden: true) {
					paragraph "<b>Notes:</b>"
					paragraph "This app is designed to give a personal welcome announcement after you have entered the home."
					paragraph "<b>Requirements:</b>"
					paragraph "Be sure to enter in the Preset Values in Advanced Config before creating Child Apps."
        				
				}
  				section("Child Apps", hideable: true, hidden: true){
					app(name: "anyOpenApp", appName: "Welcome Home Child", namespace: "BPTWorld", title: "<b>Add a new 'Welcome Home' child</b>", multiple: true)
  			    }
   				 section(" "){}
 			 	section("App Name"){
       				label title: "Enter a name for parent app (optional)", required: false
 				}
				section("<b>Be sure to enter in the Preset Values in Advanced Config before creating Child Apps</b>") {}
            		section("Advanced Config:", hideable: true, hidden: true) {
						paragraph "Be sure to fill in each field, even if it's a duplicate message.<br>%greeting% - will return a greeting based on time of day.<br>%name% - will return the Friendly Name entered within the each Child App<br>Note: adding a . anywhere will give the message a little pause"
						input "greeting1", "text", required: true, title: "Greeting - 1 (am)", defaultValue: "Good Morning"
                    	input "greeting2", "text", required: true, title: "Greeting - 2 (before 6pm)", defaultValue: "Good Afternoon"
                    	input "greeting3", "text", required: true, title: "Greeting - 3 (after 6pm)", defaultValue: "Good Evening"
						paragraph "<br>"
            			input "msg1", "text", required: true, title: "Message - 1", defaultValue: "Welcome home. %name%"
                    	input "msg2", "text", required: true, title: "Message - 2", defaultValue: "Long time no see. %name%"
                    	input "msg3", "text", required: true, title: "Message - 3", defaultValue: "Look who's home. it's %name%"
                    	input "msg4", "text", required: true, title: "Message - 4", defaultValue: "Nice to have you back. %name%"
                    	input "msg5", "text", required: true, title: "Message - 5", defaultValue: "%greeting%. %name%"
                    	input "msg6", "text", required: true, title: "Message - 6", defaultValue: "%greeting%. Oh ya. %name% is home"
                    	input "msg7", "text", required: true, title: "Message - 7", defaultValue: "How are you doing. %name%"
                    	input "msg8", "text", required: true, title: "Message - 8", defaultValue: "%greeting%. Anything I can do for you. %name%"
                    	input "msg9", "text", required: true, title: "Message - 9", defaultValue: "%greeting% I'm at your service, %name%"
                    	input "msg10", "text", required: true, title: "Message - 10", defaultValue: "%greeting%. The dogs have been waiting for you. %name%"
                	}
				}
		}
}

def installCheck(){         
	state.appInstalled = app.getInstallationState() 
	if(state.appInstalled != 'COMPLETE'){
		section{paragraph "Please hit 'Done' to install '${app.label}' parent app "}
  	}
  	else{
    	log.info "Parent Installed OK"
  	}
}

def display(){
	section{paragraph "Version: 1.0.0<br>@BPTWorld"}     
}         

def setVersion(){
		state.InternalName = "WelcomeHomeParent"  
}
