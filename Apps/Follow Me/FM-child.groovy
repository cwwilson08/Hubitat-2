/**
 *  ****************  Follow Me App  ****************
 *  Design Usage:
 *  Never miss a message again. Send messages to your occupied room speakers when home or by pushover when away. Automatically!
 *
 *  Copyright 2019 Bryan Turcotte (@bptworld)
 * 
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 * 
 *  Unless noted in the code, ALL code contained within this app is mine. You are free to change, ripout, copy, modify or
 *  otherwise use the code in anyway you want. This is a hobby, I'm more than happy to share what I have learned and help
 *  the community grow. Have FUN with it!
 * 
 *-------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  V1.2.1 - 06/26/19 - Fixed problem with testing Random Voice
 *  V1.2.0 - 06/26/19 - Added more sound options. Gave voice and sound options their own pages and added Test buttons to each.
 *  V1.1.9 - 06/25/19 - Can now select multiple switches to activate speaker
 *  V1.1.8 - 06/11/19 - Fixed when priority settings are visible
 *  V1.1.7 - 06/09/19 - Code changes to better handle priority messages. Added sounds for speech synth devices.
 *  V1.1.6 - 05/14/19 - Changed voice options to just one Fun [F] and a Random [R]
 *  V1.1.5 - 05/11/19 - Added two more voice options, just for fun! - F1 and F2
 *  V1.1.4 - 05/09/19 - Added ability to change the voice used by priority - speechSynth only
 *  V1.1.3 - 04/30/19 - Attempt to fix bug in checkTime
 *  V1.1.2 - 04/15/19 - More Code cleanup
 *  V1.1.1 - 04/06/19 - Code cleanup
 *  V1.1.0 - 04/04/19 - More tweaks
 *  V1.0.9 - 04/03/19 - More tweaks to speaker status
 *	V1.0.8 - 04/02/19 - App now sends speaker status to the driver, can be displayed on dashboards
 *	V1.0.7 - 04/02/19 - More minor tweaks. Added import URL
 *	V1.0.6 - 04/01/19 - Fixed 'Enable/Disable Switch' and Activate by 'Switch'
 *	V1.0.5 - 03/31/19 - Fixed 'Always_On' Speakers
 *	V1.0.4 - 03/28/19 - Minor Tweaks
 *	V1.0.3 - 03/27/19 - Added volume control based on message priority.
 *	V1.0.2 - 03/20/19 - Added another Google Initialize option, every x minutes
 *  V1.0.1 - 03/19/19 - Fixed a typo, trying to fix the always on
 *  V1.0.0 - 03/17/19 - Initial release.
 *
 */

def setVersion() {
	state.version = "v1.2.1"
}

definition(
    name: "Follow Me Child",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "Never miss a message again. Send messages to your occupied room speakers when home or by pushover when away. Automatically!",
    category: "",
	parent: "BPTWorld:Follow Me",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
	importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Apps/Follow%20Me/FM-child.groovy",
)

preferences {
    page(name: "pageConfig")
	page name: "pushOptions", title: "", install: false, uninstall: true, nextPage: "pageConfig"
    page name: "voiceOptions", title: "", install: false, uninstall: true, nextPage: "pageConfig"
    page name: "soundOptions", title: "", install: false, uninstall: true, nextPage: "pageConfig"
}

def pageConfig() {
    dynamicPage(name: "", title: "<h2 style='color:#1A77C9;font-weight: bold'>Follow Me</h2>", install: true, uninstall: true, refreshInterval:0) {
		display()
		getVoices()
        section("Instructions:", hideable: true, hidden: true) {
			paragraph "<b>Notes:</b>"
			paragraph "- Create a new child app for each room that has a speaker in it.<br>- Pushover child app can have up to 5 sensors defined.<br>- If more than 5 sensors are needed, simply add another child device."
			paragraph "<b>Priority Messages</b>"
			paragraph "- Each message sent to 'Follow Me' can have a priority assigned to it.<br>- Volume levels can then be adjusted by priority level.<br>- ie. (l)Dogs are hungry;(m)Door has been open too long;(h)Heat is on and window is open"
			paragraph "<b>Requirements:</b>"
			paragraph "- Virtual Device using our custom 'What Did I Say' driver"
		}
		section(getFormat("header-green", "${getImage("Blank")}"+" Message destination")) {
    		input "messageDest", "enum", title: "Select message destination", submitOnChange: true, options: ["Speakers","Pushover"], required: true
		}
		// Speakers
		if(messageDest == "Speakers") {
			section(getFormat("header-green", "${getImage("Blank")}"+" Activation Type for Room Speakers")) {
    			input "triggerMode", "enum", title: "Select message activation Type", submitOnChange: true, options: ["Always_On","Contact_Sensor","Motion_Sensor","Switch"], required: true, Multiple: false
				if(triggerMode == "Always_On"){
					paragraph "Selected speakers will always play messages."	
				}
				if(triggerMode == "Contact_Sensor"){
					input "myContacts", "capability.contactSensor", title: "Select the contact sensor(s) to activate the speaker", required: true, multiple: true
					input "contactOption", "enum", title: "Select contact option - If (option), Speaker is On", options: ["Open","Closed"], required: true
					input "sZoneWaiting", "number", title: "After contact changes, wait X minutes to turn the speaker off", required: true, defaultValue: 5
				}
				if(triggerMode == "Motion_Sensor"){
					input "myMotion", "capability.motionSensor", title: "Select the motion sensor(s) to activate the speaker", required: true, multiple: true
					input "sZoneWaiting", "number", title: "After motion stops, wait X minutes to turn the speaker off", required: true, defaultValue: 5
				}
				if(triggerMode == "Switch"){
					input "mySwitches", "capability.switch", title: "Select Switch(es) to activate the speaker", required: true, multiple: true
					input "sZoneWaiting", "number", title: "After Switch is off, wait X minutes to turn the speaker off", required: true, defaultValue: 5
				}
			}
			section(getFormat("header-green", "${getImage("Blank")}"+" Speaker Options")) {
        	   	input "speechMode", "enum", required: true, title: "Select Speaker Type", submitOnChange: true,  options: ["Music Player", "Speech Synth"] 
				if (speechMode == "Music Player"){ 
                    paragraph "NOTE: Music Player is typically used with Echo and Sonos devices but others may work. (Not Google/Nest)"
            	  	input "speaker", "capability.musicPlayer", title: "Choose speaker", required: true, submitOnChange: true
					input(name: "echoSpeaks", type: "bool", defaultValue: "false", title: "Is this an 'echo speaks' device?", description: "Echo speaks device?", submitOnChange: true)
          		}   
        		if (speechMode == "Speech Synth"){
                    paragraph "NOTE: Speech Synth is typically used with Google devices but others may work. (Not Echo)"
         			input "speaker", "capability.speechSynthesis", title: "Choose speaker", required: true, submitOnChange: true
					input(name: "gSpeaker", type: "bool", defaultValue: "false", title: "Is this a Google device?", description: "Google device?", submitOnChange: true)
					if(gSpeaker) paragraph "If using Google speaker devices sometimes an Initialize is necessary (not always)."
					if(gSpeaker) input "gInitialize", "bool", title: "Initialize Google devices before sending speech?", required: true, defaultValue: false
					if(gSpeaker) input "gInitRepeat", "number", title: "Initialize Google devices every X minutes?", required: false
         	 	}
      		}
			section(getFormat("header-green", "${getImage("Blank")}"+" Volume Control Options")) {
				paragraph "NOTE: Not all speakers can use volume controls. If you would like to use volume controls with Echo devices please use the app 'Echo Speaks' and then choose the 'Music Player' option instead of Spech Synth."
				input "volSpeech", "number", title: "Speaker volume for speech", description: "0-100", required: true
				input "volRestore", "number", title: "Restore speaker volume to X after speech", description: "0-100", required: true
            	input "volQuiet", "number", title: "Quiet Time Speaker volume", description: "0-100", required: false, submitOnChange: true
				if(volQuiet) input "QfromTime", "time", title: "Quiet Time Start", required: true
    		 	if(volQuiet) input "QtoTime", "time", title: "Quiet Time End", required: true
			}
			section(getFormat("header-green", "${getImage("Blank")}"+" Speech Options")) {
				input(name: "messagePriority", type: "bool", defaultValue: "false", title: "Use Message Priority features?", description: "Message Priority", submitOnChange: true)
				if((messagePriority) && (speechMode == "Speech Synth")) input(name: "priorityVoices", type: "bool", defaultValue: "false", title: "Use different voices for each Priority level?", description: "Priority Voices", submitOnChange: true)
				if((messagePriority) && (speechMode == "Speech Synth")) input(name: "messageSounds", type: "bool", defaultValue: "false", title: "Play a sound before message?", description: "Message Sounds", submitOnChange: true)
			}
			if(messagePriority) {
				section("Instructions for Message Priority:", hideable: true, hidden: true) {
					paragraph "<b>Notes:</b>"
					paragraph "Message Priority is a unique feature only found with 'Follow Me'! Simply place one of the following options in front of any message to be spoken and the volume and/or voice will be adjusted accordingly.<br><b>[F]</b> - Fun<br><b>[R]</b> - Random<br><b>[L]</b> - Low<br><b>[M]</b> - Medium<br><b>[H]</b> - High"
					paragraph "You can also specify a sound file to be played before a message!<br><br><b>[1] - [5]</b> - Specify a files URL"
					paragraph "<b>Options can also be combined!</b> ie. [R2], [L2], etc."
					paragraph "ie. [L]Amy is home or [M3]Window has been open too long or [H]Heat is on and window is open"
					paragraph "If you JUST want a sound file played with NO speech after, use [L1]. or [M3]. etc. Notice the DOT after the [], that is the message and will not be spoken."
					paragraph "Also notice there is no spaces between the option and the message."
				}
				section() {
					paragraph "Low priority will use the standard volume set in the Volume Control Section"
					input "volMed", "number", title: "Speaker volume for Medium priority", description: "0-100", required: true, width: 6
					input "volHigh", "number", title: "Speaker volume for High priority", description: "0-100", required: true, width: 6
				}
				if(speechMode == "Speech Synth") {
                    if(priorityVoices) {
                        section(getFormat("header-green", "${getImage("Blank")}"+" Voice Options")) {
                            href "voiceOptions", title:"Voice Options Setup", description:"Click here to setup the voices"
                        }
					}
				    if(messageSounds) {
					    section(getFormat("header-green", "${getImage("Blank")}"+" Sound Options")) {
					    	href "soundOptions", title:"Sound Options Setup", description:"Click here to setup the sound files"
					    }
                    }
				} else {
					section() {
						paragraph "* Priority Voice and Sound options are only available when using Speech Synth option."
					}
				}
			}
    		if(speechMode){ 
				section(getFormat("header-green", "${getImage("Blank")}"+" Allow messages between what times? (Optional)")) {
        			input "fromTime", "time", title: "From", required: false
        			input "toTime", "time", title: "To", required: false
				}
    		}
		}
		// both Speakers and Pushover
		section(getFormat("header-green", "${getImage("Blank")}"+" Speech Device")) {
			paragraph "This app requires a 'virtual device' to 'catch' the speech and send it here. All child apps will share this device. If you already use our 'What Did I Say' driver...you're allset! Just select the same device used with 'What Did I Say'."
			paragraph "* Vitual Device must use our custom 'What Did I Say Driver'"
			input "gvDevice", "capability.speechSynthesis", title: "Virtual Device created for Follow Me", required: true, multiple: false
		}
		// Pushover
		if(messageDest == "Pushover") {
			section(getFormat("header-green", "${getImage("Blank")}"+" Presence Options for Pushover Messages")) {
				href "pushOptions", title:"Presence and Pushover Setup", description:"Select up to 5 presence sensor / pushover combinations"
			}
		}
		// both Speakers and Pushover
		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {label title: "Enter a name for this automation", required: false}
        section() {
            input(name: "logEnable", type: "bool", defaultValue: "false", title: "Enable Debug Logging", description: "Enable extra logging for debugging.")
		}
		display2()
	}
}

def pushOptions(){
    dynamicPage(name: "pushOptions", title: "Presence and Pushover Setup", install: false, uninstall:false){
		section(getFormat("header-green", "${getImage("Blank")}"+" Presence Options for Pushover Messages")) {
			paragraph "Select up to 5 presence sensor/Pushover Device combinations to receive messages when away from home."
			paragraph "<b>Combination 1</b>"
			input("presenceSensor1", "capability.presenceSensor", title: "Presence Sensor 1", required: false, width: 6)
			input("sendPushMessage1", "capability.notification", title: "Pushover Device 1", required: false, width: 6)
			paragraph "<b>Combination 2</b>"
			input("presenceSensor2", "capability.presenceSensor", title: "Presence Sensor 2", required: false, width: 6)
			input("sendPushMessage2", "capability.notification", title: "Pushover Device 2", required: false, width: 6)
			paragraph "<b>Combination 3</b>"
			input("presenceSensor3", "capability.presenceSensor", title: "Presence Sensor 3", required: false, width: 6)
			input("sendPushMessage3", "capability.notification", title: "Pushover Device 3", required: false, width: 6)
			paragraph "<b>Combination 4</b>"
			input("presenceSensor4", "capability.presenceSensor", title: "Presence Sensor 4", required: false, width: 6)
			input("sendPushMessage4", "capability.notification", title: "Pushover Device 4", required: false, width: 6)
			paragraph "<b>Combination 5</b>"
			input("presenceSensor5", "capability.presenceSensor", title: "Presence Sensor 5", required: false, width: 6)
			input("sendPushMessage5", "capability.notification", title: "Pushover Device 5", required: false, width: 6)
		}
	}
}		

def voiceOptions(){
    dynamicPage(name: "voiceOptions", title: "Voice Option Setup", install: false, uninstall:false){
		section("Select Voices for different priorities") {
            input "testSpeaker", "capability.speechSynthesis", title: "Choose speaker for testing", required: true, submitOnChange: true
            input "testPhrase", "text", title: "Test Phrase", required: true, defaultValue: "This is a test", submitOnChange: true              
			input "voiceFun", "enum", title: "Select Voice for priority - Fun", options: state.list, required: false, submitOnChange: true, width: 7
            if(voiceFun) input "testVoiceFun", "button", title: "Test Voice Fun", width: 5
			input "voiceRandom", "enum", title: "Select Voice for priority - Random", options: state.list, required: false, multiple: true, submitOnChange: true, width: 7
            if(voiceRandom) input "testVoiceRandom", "button", title: "Test Voice Random", width: 5
			input "voiceLow", "enum", title: "Select Voice for priority - Low", options: state.list, required: false, submitOnChange: true, width: 7
            if(voiceLow) input "testVoiceLow", "button", title: "Test Voice Low", width: 5
			input "voiceMed", "enum", title: "Select Voice for priority - Medium", options: state.list, required: false, submitOnChange: true, width: 7
            if(voiceMed) input "testVoiceMed", "button", title: "Test Voice Med", width: 5
		    input "voiceHigh", "enum", title: "Select Voice for priority - High", options: state.list, required: false, submitOnChange: true, width: 7
            if(voiceHigh) input "testVoiceHigh", "button", title: "Test Voice High", width: 5
        }
	}
}	

def soundOptions(){
    dynamicPage(name: "soundOptions", title: "Sound Option Setup", install: false, uninstall:false){
		section(getFormat("header-green", "${getImage("Blank")}"+" Sound Options")) {
			paragraph "Link to any sound file you want.  ie. http://192.168.7.89:820/fastpops1.mp3"
            input "testSpeaker", "capability.speechSynthesis", title: "Choose speaker for testing", required: true, submitOnChange: true
			input "sound1", "text", title: "Sound - 1", required: false, width: 9
            if(sound1) input "testBtn1", "button", title: "Test Sound 1", width: 3
			input "sound2", "text", title: "Sound - 2", required: false, width: 9
            if(sound2) input "testBtn2", "button", title: "Test Sound 2", width: 3
			input "sound3", "text", title: "Sound - 3", required: false, width: 9
            if(sound3) input "testBtn3", "button", title: "Test Sound 3", width: 3
			input "sound4", "text", title: "Sound - 4", required: false, width: 9
            if(sound4) input "testBtn4", "button", title: "Test Sound 4", width: 3
			input "sound5", "text", title: "Sound - 5", required: false, width: 9
            if(sound5) input "testBtn5", "button", title: "Test Sound 5", width: 3
            input "sound6", "text", title: "Sound - 6", required: false, width: 9
            if(sound6) input "testBtn6", "button", title: "Test Sound 6", width: 3
            input "sound7", "text", title: "Sound - 7", required: false, width: 9
            if(sound7) input "testBtn7", "button", title: "Test Sound 7", width: 3
            input "sound8", "text", title: "Sound - 8", required: false, width: 9
            if(sound8) input "testBtn8", "button", title: "Test Sound 8", width: 3
            input "sound9", "text", title: "Sound - 9", required: false, width: 9
            if(sound9) input "testBtn9", "button", title: "Test Sound 9", width: 3
            input "sound10", "text", title: "Sound - 10", required: false, width: 9
            if(sound10) input "testBtn10", "button", title: "Test Sound 10", width: 3
		}
	}
}		

def installed() {
    log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {	
    if(logEnable) log.debug "Updated with settings: ${settings}"
    unsubscribe()
	initialize()
}

def initialize() {
    setDefaults()
	subscribe(gvDevice, "lastSpokenUnique", lastSpokenHandler)
	if(myContact) subscribe(myContacts, "contact", contactSensorHandler)
	if(myMotion) subscribe(myMotion, "motion", motionSensorHandler)
	if(mySwitches) subscribe(mySwitches, "switch", switchHandler)
	if(presenceSensor1) subscribe(presenceSensor1, "presence", presenceSensorHandler1)
	if(presenceSensor2) subscribe(presenceSensor2, "presence", presenceSensorHandler2)
	if(presenceSensor3) subscribe(presenceSensor3, "presence", presenceSensorHandler3)
	if(presenceSensor4) subscribe(presenceSensor4, "presence", presenceSensorHandler4)
	if(presenceSensor5) subscribe(presenceSensor5, "presence", presenceSensorHandler5)
	if(gInitRepeat) runIn(gInitRepeat,initializeSpeaker)
}

def presenceSensorHandler1(evt){
	state.presenceSensorValue1 = evt.value
	if(logEnable) log.debug "In presenceSensorHandler1 - Presence Sensor: ${state.presenceSensorValue1}"
    if(state.presenceSensorValue1 == "not present"){
    	if(logEnable) log.debug "Presence Sensor 1 is not present."
		state.IH1 = "no"
    } else {
		if(logEnable) log.debug "Presence Sensor 1 is present."
		state.IH1 = "yes"
    }
}

def presenceSensorHandler2(evt){
	state.presenceSensorValue2 = evt.value
	if(logEnable) log.debug "In presenceSensorHandler2 - Presence Sensor: ${state.presenceSensorValue2}"
    if(state.presenceSensorValue2 == "not present"){
    	if(logEnable) log.debug "Presence Sensor 2 is not present."
		state.IH2 = "no"
    } else {
		if(logEnable) log.debug "Presence Sensor 2 is present."
		state.IH2 = "yes"
    }
}

def presenceSensorHandler3(evt){
	state.presenceSensorValue3 = evt.value
	if(logEnable) log.debug "In presenceSensorHandler3 - Presence Sensor: ${state.presenceSensorValue3}"
    if(state.presenceSensorValue3 == "not present"){
    	if(logEnable) log.debug "Presence Sensor 3 is not present."
		state.IH3 = "no"
    } else {
		if(logEnable) log.debug "Presence Sensor 3 is present."
		state.IH3 = "yes"
    }
}

def presenceSensorHandler4(evt){
	state.presenceSensorValue4 = evt.value
	if(logEnable) log.debug "In presenceSensorHandler4 - Presence Sensor: ${state.presenceSensorValue4}"
    if(state.presenceSensorValue4 == "not present"){
    	if(logEnable) log.debug "Presence Sensor 4 is not present."
		state.IH4 = "no"
    } else {
		if(logEnable) log.debug "Presence Sensor 4 is present."
		state.IH4 = "yes"
    }
}

def presenceSensorHandler5(evt){
	state.presenceSensorValue5 = evt.value
	if(logEnable) log.debug "In presenceSensorHandler5 - Presence Sensor: ${state.presenceSensorValue5}"
    if(state.presenceSensorValue5 == "not present"){
    	if(logEnable) log.debug "Presence Sensor 5 is not present."
		state.IH5 = "no"
    } else {
		if(logEnable) log.debug "Presence Sensor 5 is present."
		state.IH5 = "yes"
    }
}

def alwaysOnHandler() {
	if(logEnable) log.debug "In alwaysOnHandler..."
	if(logEnable) log.debug "In alwaysOnHandler - setting sZone to true"
	atomicState.sZone = true
	speakerStatus = "${app.label}:${atomicState.sZone}"
	gvDevice.sendFollowMeSpeaker(speakerStatus)
}

def contactSensorHandler(evt) {
	if(logEnable) log.debug "In contactSensorHandler..."
	state.contactStatus = evt.value
	if(logEnable) log.debug "In contactSensorHandler - sZone: ${atomicState.sZone} - Status: ${state.contactStatus}"
	if(contactOption == "Closed") {
		if(state.contactStatus == "closed") {
			if(logEnable) log.debug "In contactSensorHandler - setting sZone to true"
			atomicState.sZone = true
			speakerStatus = "${app.label}:${atomicState.sZone}"
			gvDevice.sendFollowMeSpeaker(speakerStatus)
		}
		if(state.contactStatus == "open") {
			sOff = sZoneWaiting * 60
			runIn(sOff,speechOff)
		}
	}
	if(contactOption == "Open") {
		if(state.contactStatus == "open") {
			if(logEnable) log.debug "In contactSensorHandler - setting sZone to true"
			atomicState.sZone = true
			speakerStatus = "${app.label}:${atomicState.sZone}"
			gvDevice.sendFollowMeSpeaker(speakerStatus)
		}
		if(state.contactStatus == "closed") {
			sOff = sZoneWaiting * 60
			runIn(sOff,speechOff)
		}
	}
}

def motionSensorHandler(evt) {
	if(logEnable) log.debug "In motionSensorHandler..."
	state.motionStatus = evt.value
	if(logEnable) log.debug "In motionSensorHandler - sZone: ${atomicState.sZone} - Status: ${state.motionStatus}"
	if(state.motionStatus == "active") {
		if(logEnable) log.debug "In motionSensorHandler - setting sZone to true"
		atomicState.sZone = true
		speakerStatus = "${app.label}:${atomicState.sZone}"
		gvDevice.sendFollowMeSpeaker(speakerStatus)
	}
	if(state.motionStatus == "inactive") {
		sOff = sZoneWaiting * 60
		runIn(sOff,speechOff)
	}
}

def switchHandler(evt) {
	if(logEnable) log.debug "In switchHandler..."
	state.switchStatus = evt.value
	if(logEnable) log.debug "In switchHandler - sZone: ${atomicState.sZone} - Status: ${state.switchStatus}"
	if(state.switchStatus == "on") {
		if(logEnable) log.debug "In switchHandler - setting sZone to true"
		atomicState.sZone = true
		speakerStatus = "${app.label}:${atomicState.sZone}"
		gvDevice.sendFollowMeSpeaker(speakerStatus)
	}
	if(state.switchStatus == "off") {
		sOff = sZoneWaiting * 60
		runIn(sOff,speechOff)
	}
}

def lastSpokenHandler(speech) { 
	if(logEnable) log.debug "In lastSpoken..."
	if(triggerMode == "Always_On") alwaysOnHandler()
	state.unique = speech.value.toString()
	state.cleanUp = state.unique.drop(1)
	if(state.cleanUp.contains("]")) {
		def (priority, msgA) = state.cleanUp.split(']')
		state.priority = priority.drop(1)
		state.lastSpoken = msgA
	} else{
		state.lastSpoken = state.cleanUp
	}
	if(state.lastSpoken == null) state.lastSpoken = ""
	if(logEnable) log.debug "In lastSpoken - Priority: ${state.priority} - lastSpoken: ${state.lastSpoken}"
	letsTalk()
	sendPush()
}

def speechOff() {
	if(state.motionStatus == 'active'){
		atomicState.sZone = true
		if(logEnable) log.debug "In speechOff - Speech is on - sZone: ${atomicState.sZone}"
	} else{
		atomicState.sZone = false
		speakerStatus = "${app.label}:${atomicState.sZone}"
		gvDevice.sendFollowMeSpeaker(speakerStatus)
		if(logEnable) log.debug "In speechOff - Speech is off - sZone: ${atomicState.sZone}"
	}
}

def initializeSpeaker() {
	if(logEnable) log.debug "In initializeSpeaker - Initializing ${speaker}"
	speaker.initialize()
	if(gInitRepeat) repeat = gInitRepeat * 60
	if(gInitRepeat) runIn(repeat,initializeSpeaker)
}
						  					  
def letsTalk() {
	if(logEnable) log.debug "In letsTalk..."
	if(triggerMode == "Always_On") alwaysOnHandler()
	if(atomicState.sZone == true){
		checkTime()
		checkVol()
		atomicState.randomPause = Math.abs(new Random().nextInt() % 1500) + 400
		if(logEnable) log.debug "In letsTalk - pause: ${atomicState.randomPause}"
		pauseExecution(atomicState.randomPause)
		if(logEnable) log.debug "In letsTalk - continuing"
		if(state.timeBetween == true) {
			state.sStatus = "speaking"
			speakerStatus = "${app.label}:${state.sStatus}"
			gvDevice.sendFollowMeSpeaker(speakerStatus)
			if(logEnable) log.debug "In letsTalk - ${speechMode} - ${speaker}"
  			if (speechMode == "Music Player"){ 
				if(echoSpeaks) {
					speaker.setVolumeSpeakAndRestore(state.volume, state.lastSpoken, volRestore)
				}
				if(!echoSpeaks) {
    				if(volSpeech) speaker.setLevel(state.volume)
    				speaker.playTextAndRestore(state.lastSpoken, volRestore)
				}
  			}   
			if (speechMode == "Speech Synth"){
				speechDuration = Math.max(Math.round(state.lastSpoken.length()/12),2)+3		// Code from @djgutheinz
				atomicState.speechDuration2 = speechDuration * 1000
				if(gInitialize) initializeSpeaker()
				if(volSpeech) speaker.setVolume(state.volume)
				if(priorityVoices) {
					priorityVoicesHandler()
				} else {
					if(logEnable) log.debug "In letsTalk - Using Hubitat's default voice"
					speaker.speak(state.lastSpoken)
				}
				pauseExecution(atomicState.speechDuration2)
				if(volRestore) {
					speaker.setVolume(volRestore)
				}
			}
			speakerStatus = "${app.label}:${atomicState.sZone}"
			gvDevice.sendFollowMeSpeaker(speakerStatus)
			log.info "${app.label} - ${state.lastSpoken}"
			if(logEnable) log.debug "In letsTalk...Okay, I'm done!"
		} else {
			log.info "${app.label} - Quiet Time, can not speak."
		}
	} else {
		log.info "${app.label} - Zone is Off, can not speak."
	}
}

def checkTime() {
	if(logEnable) log.debug "In checkTime - ${fromTime} - ${toTime}"
	if(fromTime) {
		state.betweenTime = timeOfDayIsBetween(toDateTime(fromTime), toDateTime(toTime), new Date(), location.timeZone)
		if(state.betweenTime) {
			state.timeBetween = true
		} else {
			state.timeBetween = false
		}
  	} else {  
		state.timeBetween = true
  	}
	if(logEnable) log.debug "In checkTime - timeBetween: ${state.timeBetween}"
}

def checkVol() {
	if(logEnable) log.debug "In checkVol..."
	if(QfromTime) {
		state.quietTime = timeOfDayIsBetween(toDateTime(QfromTime), toDateTime(QtoTime), new Date(), location.timeZone)
    	if(state.quietTime) {
    		state.volume = volQuiet
		} else {
			state.volume = volSpeech
		}
	} else {
		state.volume = volSpeech
	}
	if(logEnable) log.debug "In checkVol - volume: ${state.volume}"
	if(messagePriority) {
		if(logEnable) log.debug "In checkVol - priority: ${state.priority}"
		if(state.priority.toLowerCase().contains("f")) {
			state.voiceSelected = voiceFun
		}
		if(state.priority.toLowerCase().contains("r")) {
			randomHandler()
			state.voiceSelected = state.randVoice
		}
		if(state.priority.toLowerCase().contains("l")) {
			state.voiceSelected = voiceLow
		}
		if(state.priority.toLowerCase().contains("m")) {
			state.volume = volMed
			state.voiceSelected = voiceMed
		}
		if(state.priority.toLowerCase().contains("h")) {
			state.volume = volHigh
			state.voiceSelected = voiceHigh
		}
		if(logEnable) log.debug "In checkVol - priority volume: ${state.volume}"
	}
}

def priorityVoicesHandler() {
    if(state.lastSpoken == ".") state.lastSpoken = ""
	if(logEnable) log.debug "In priorityVoicesHandler - Changing voice to ${state.voiceSelected} - Message: ${state.lastSpoken}"
	def tts = textToSpeech(state.lastSpoken,state.voiceSelected)
	def uriMessage = "${tts.get('uri')}"
	if(state.priority.contains("1")) {
		if(sound1) {
			speaker.playTrack(sound1)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 1 not defined"
	}
	if(state.priority.contains("2")) {
		if(sound2) {
			speaker.playTrack(sound2)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 2 not defined"
	}
	if(state.priority.contains("3")) {
		if(sound3) {
			speaker.playTrack(sound3)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 3 not defined"
	}
	if(state.priority.contains("4")) {
		if(sound4) {
			speaker.playTrack(sound4)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 4 not defined"
	}
	if(state.priority.contains("5")) {
		if(sound5) {
			speaker.playTrack(sound5)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 5 not defined"
	}
    if(state.priority.contains("6")) {
		if(sound6) {
			speaker.playTrack(sound6)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 6 not defined"
	}
    if(state.priority.contains("7")) {
		if(sound7) {
			speaker.playTrack(sound7)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 7 not defined"
	}
    if(state.priority.contains("8")) {
		if(sound8) {
			speaker.playTrack(sound8)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 8 not defined"
	}
    if(state.priority.contains("9")) {
		if(sound9) {
			speaker.playTrack(sound9)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 9 not defined"
	}
    if(state.priority.contains("10")) {
		if(sound10) {
			speaker.playTrack(sound10)
			pauseExecution(1000)
		} else log.info "${app.label} - Sound 10 not defined"
	}
    
	if(logEnable) log.debug "In priorityVoicesHandler - ${uriMessage}"
	speaker.playTrack(uriMessage)
}

def sendPush() {
	if(logEnable) log.debug "In sendPush - ${state.lastSpoken}"
	if(state.IH1 == "no") {
		theMessage = "${state.lastSpoken}"
		if(logEnable) log.debug "In sendPush - IH1 Sending message: ${theMessage}"
    	sendPushMessage1.deviceNotification(theMessage)
	}
	if(state.IH2 == "no") {
		theMessage = "${state.lastSpoken}"
		if(logEnable) log.debug "In sendPush - IH2 Sending message: ${theMessage}"
    	sendPushMessage2.deviceNotification(theMessage)
	}
	if(state.IH3 == "no") {
		theMessage = "${state.lastSpoken}"
		if(logEnable) log.debug "In sendPush - IH3 Sending message: ${theMessage}"
    	sendPushMessage3.deviceNotification(theMessage)
	}
	if(state.IH4 == "no") {
		theMessage = "${state.lastSpoken}"
		if(logEnable) log.debug "In sendPush - IH4 Sending message: ${theMessage}"
    	sendPushMessage4.deviceNotification(theMessage)
	}
	if(state.IH5 == "no") {
		theMessage = "${state.lastSpoken}"
		if(logEnable) log.debug "In sendPush - IH5 Sending message: ${theMessage}"
    	sendPushMessage5.deviceNotification(theMessage)
	}
}

def getVoices(){						// Modified from @mike.maxwell
	if(logEnable) log.debug "In getVoices..."
	def voices = getTTSVoices()
	voices.sort{ a, b ->
		a.language <=> b.language ?: a.gender <=> b.gender ?: a.gender <=> b.gender  
	}    
    state.list = voices.collect{ ["${it.name}": "${it.language}:${it.gender}:${it.name}"] }
}

def randomHandler() {
	if(logEnable) log.debug "In randomHandler..."
	vSize = voiceRandom.size()
	count = vSize.toInteger()
    def randomKey = new Random().nextInt(count)
	state.randVoice = voiceRandom[randomKey]
	if(logEnable) log.debug "In randomHandler - Random - vSize: ${vSize}, randomKey: ${randomKey}, randomVoice: ${state.randVoice}"
}

def appButtonHandler(buttonPressed) {
    state.whichButton = buttonPressed
    if(logEnable) log.debug "In testButtonHandler - Button Pressed: ${state.whichButton}"
    if(state.whichButton == "testBtn1"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 1"
        testSpeaker.playTrack(sound1)
    }
    if(state.whichButton == "testBtn2"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 2"
        testSpeaker.playTrack(sound2)
    }
    if(state.whichButton == "testBtn3"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 3"
        testSpeaker.playTrack(sound3)
    }
    if(state.whichButton == "testBtn4"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 4"
        testSpeaker.playTrack(sound4)
    }
    if(state.whichButton == "testBtn5"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 5"
        testSpeaker.playTrack(sound5)
    }
    if(state.whichButton == "testBtn6"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 6"
        testSpeaker.playTrack(sound6)
    }
    if(state.whichButton == "testBtn7"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 7"
        testSpeaker.playTrack(sound7)
    }
    if(state.whichButton == "testBtn8"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 8"
        testSpeaker.playTrack(sound8)
    }
    if(state.whichButton == "testBtn9"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 9"
        testSpeaker.playTrack(sound9)
    }
    if(state.whichButton == "testBtn10"){
        if(logEnable) log.debug "In testButtonHandler - Testing Sound 10"
        testSpeaker.playTrack(sound10)
    }
    if(state.whichButton == "testVoiceFun"){
        if(logEnable) log.debug "In testButtonHandler - Testing Voice Fun"
        def tts = textToSpeech(testPhrase,voiceFun)
	    def uriMessage = "${tts.get('uri')}"
        testSpeaker.playTrack(uriMessage)
    }
    if(state.whichButton == "testVoiceRandom"){
        if(logEnable) log.debug "In testButtonHandler - Testing Voice Random"
        randomHandler()
        def tts = textToSpeech(testPhrase,state.randVoice)
	    def uriMessage = "${tts.get('uri')}"
        testSpeaker.playTrack(uriMessage)
    }
    if(state.whichButton == "testVoiceLow"){
        if(logEnable) log.debug "In testButtonHandler - Testing Voice Low"
        def tts = textToSpeech(testPhrase,voiceLow)
	    def uriMessage = "${tts.get('uri')}"
        testSpeaker.playTrack(uriMessage)
    }
    if(state.whichButton == "testVoiceMed"){
        if(logEnable) log.debug "In testButtonHandler - Testing Voice Med"
        def tts = textToSpeech(testPhrase,voiceMed)
	    def uriMessage = "${tts.get('uri')}"
        testSpeaker.playTrack(uriMessage)
    }
    if(state.whichButton == "testVoiceHigh"){
        if(logEnable) log.debug "In testButtonHandler - Testing Voice High"
        def tts = textToSpeech(testPhrase,voiceHigh)
	    def uriMessage = "${tts.get('uri')}"
        testSpeaker.playTrack(uriMessage)
    }
}   

// ********** Normal Stuff **********

def setDefaults(){
	if(logEnable) log.debug "In setDefaults..."
	if(logEnable == null){logEnable = false}
	if(messagePriority == null){messagePriority = false}
	if(atomicState.sZone == null){atomicState.sZone = false}
	if(state.IH1 == null){state.IH1 = "blank"}
	if(state.IH2 == null){state.IH2 = "blank"}
	if(state.IH3 == null){state.IH3 = "blank"}
	if(state.IH4 == null){state.IH4 = "blank"}
	if(state.IH5 == null){state.IH5 = "blank"}
	if(state.lastSpoken == null){state.lastSpoken = ""}
}

def getImage(type){						// Modified from @Stephack
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
}

def getFormat(type, myText=""){			// Modified from @Stephack
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "\n<hr style='background-color:#1A77C9; height: 1px; border: 0;'></hr>"
	if(type == "title") return "<div style='color:blue;font-weight: bold'>${myText}</div>"
}

def display() {
	section() {
		paragraph getFormat("line")
	}
}

def display2(){
	setVersion()
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center'>Follow Me - @BPTWorld<br><a href='https://github.com/bptworld/Hubitat' target='_blank'>Find more apps on my Github, just click here!</a><br>Get app update notifications and more with <a href='https://github.com/bptworld/Hubitat/tree/master/Apps/App%20Watchdog' target='_blank'>App Watchdog</a><br>${state.version}</div>"
	}       
}  
