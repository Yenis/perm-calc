import React from 'react';
import { View, Text, StyleSheet, ActivityIndicator, Modal } from 'react-native';

export function DemoModal({ visible, message, children }) {
  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      statusBarTranslucent
    >
      <View style={styles.overlay}>
        <View style={styles.box}>
          {children}
          <View style={styles.row}>
            <ActivityIndicator color="#4fc3f7" size="small" />
            <Text style={styles.message}>{message}</Text>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.85)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  box: {
    backgroundColor: '#1e1e2e',
    borderRadius: 20,
    width: '100%',
    padding: 20,
    alignItems: 'center',
    gap: 16,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  message: {
    color: '#b0b0c8',
    fontSize: 14,
  },
});
