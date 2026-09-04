import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, SafeAreaView } from 'react-native';
import { useTranslation } from 'react-i18next';

export function DisclaimerScreen({ onAccept }) {
  const { t } = useTranslation();

  return (
    <SafeAreaView style={styles.safe}>
      <ScrollView contentContainerStyle={styles.container} bounces={false}>
        <View style={styles.iconWrap}>
          <Text style={styles.icon}>🔐</Text>
        </View>

        <Text style={styles.appName}>{t('common.appName')}</Text>
        <Text style={styles.title}>{t('disclaimer.title')}</Text>
        <Text style={styles.subtitle}>{t('disclaimer.subtitle')}</Text>

        <View style={styles.bodyCard}>
          <Text style={styles.body}>{t('disclaimer.body')}</Text>
        </View>

        <View style={styles.noteBox}>
          <Text style={styles.noteIcon}>🛡️</Text>
          <Text style={styles.note}>{t('disclaimer.note')}</Text>
        </View>

        <TouchableOpacity style={styles.button} onPress={onAccept}>
          <Text style={styles.buttonText}>{t('common.understand')}</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: '#12121f',
  },
  container: {
    flexGrow: 1,
    alignItems: 'center',
    padding: 28,
    paddingTop: 48,
  },
  iconWrap: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: '#1e1e2e',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#3a3a52',
  },
  icon: {
    fontSize: 44,
  },
  appName: {
    fontSize: 32,
    fontWeight: '800',
    color: '#4fc3f7',
    letterSpacing: 2,
    marginBottom: 6,
  },
  title: {
    fontSize: 16,
    fontWeight: '700',
    color: '#e8e8f0',
    marginBottom: 4,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  subtitle: {
    fontSize: 14,
    color: '#7070a0',
    marginBottom: 28,
    textAlign: 'center',
  },
  bodyCard: {
    backgroundColor: '#1e1e2e',
    borderRadius: 16,
    padding: 20,
    width: '100%',
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#2a2a3e',
  },
  body: {
    fontSize: 15,
    color: '#b0b0c8',
    lineHeight: 24,
  },
  noteBox: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    backgroundColor: '#1a3a1a',
    borderRadius: 12,
    padding: 16,
    width: '100%',
    marginBottom: 32,
    gap: 10,
    borderWidth: 1,
    borderColor: '#2e7d32',
  },
  noteIcon: {
    fontSize: 18,
    marginTop: 1,
  },
  note: {
    flex: 1,
    fontSize: 13,
    color: '#a5d6a7',
    lineHeight: 20,
  },
  button: {
    backgroundColor: '#6a1b9a',
    borderRadius: 14,
    paddingVertical: 18,
    paddingHorizontal: 40,
    width: '100%',
    alignItems: 'center',
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '700',
  },
});
