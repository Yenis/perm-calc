import React from 'react';
import { TouchableNativeFeedback, View, Text, StyleSheet } from 'react-native';
import { calcColors } from '../../theme';

const ICONS = {
  camera: '📷',
  microphone: '🎙️',
  contacts: '👥',
  location: '📍',
  storage: '🗂️',
};

export function PermissionButton({ permKey, label, onPress }) {
  return (
    <View style={styles.wrapper}>
      <TouchableNativeFeedback
        onPress={onPress}
        background={TouchableNativeFeedback.Ripple('rgba(106,27,154,0.3)', false)}
      >
        <View style={styles.button}>
          <Text style={styles.icon}>{ICONS[permKey]}</Text>
          <Text style={styles.label}>{label}</Text>
        </View>
      </TouchableNativeFeedback>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    margin: 4,
    borderRadius: 10,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: calcColors.permButtonBorder,
  },
  button: {
    backgroundColor: calcColors.permButton,
    paddingVertical: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
  icon: {
    fontSize: 18,
    marginBottom: 2,
  },
  label: {
    color: '#ce93d8',
    fontSize: 10,
    fontWeight: '600',
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
});
