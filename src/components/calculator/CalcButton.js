import React from 'react';
import { TouchableNativeFeedback, View, Text, StyleSheet } from 'react-native';

export function CalcButton({ label, onPress, color, textColor, flex = 1, fontSize = 22 }) {
  return (
    <View style={[styles.wrapper, { flex }]}>
      <TouchableNativeFeedback
        onPress={onPress}
        background={TouchableNativeFeedback.Ripple('rgba(255,255,255,0.15)', false)}
      >
        <View style={[styles.button, { backgroundColor: color }]}>
          <Text style={[styles.label, { color: textColor ?? '#e8e8f0', fontSize }]}>{label}</Text>
        </View>
      </TouchableNativeFeedback>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    margin: 4,
    borderRadius: 12,
    overflow: 'hidden',
  },
  button: {
    height: 64,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 12,
  },
  label: {
    fontWeight: '500',
  },
});
