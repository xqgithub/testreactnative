import React, { Component } from 'react'
import { View, Text, StyleSheet,NativeModules } from 'react-native'
export default class extends Component {

   /**
    * 调用原生代码
    */
    skipNativeCall() {
       let phone = '13554298369';
       NativeModules.commModule.rnCallNative(phone);
    }


  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>
          Hello React Native!
        </Text>
         <Text style={styles.text} onPress={this.skipNativeCall.bind(this)}>
            跳转到拨号界面
         </Text>
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#209bf4'
  },
  text: {
    fontSize: 20,
    textAlign: 'center',
    color: '#333333'
  }
})