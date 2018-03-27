'use strict'
import { AppRegistry } from 'react-native'
import {StackNavigator} from 'react-navigation'
import React, { Component } from 'react';
import Storage from 'react-native-storage';
import App from './js/APP'
import ViewOne from './js/ViewOne'
import ViewTow from './js/ViewTow'
import ViewThree from './js/ViewThree'

const View = StackNavigator({
  One:{screen:ViewOne},
  Tow:{screen:ViewTow},
  Three:{screen:ViewThree},
})

AppRegistry.registerComponent('helloreactnative', () => App)
AppRegistry.registerComponent('testview', () => View)