import React from 'react';
import { View, Text, ScrollView, StyleSheet, TouchableOpacity, Modal } from 'react-native';
import { useTranslation } from 'react-i18next';
import { calcColors } from '../../theme';

export function InfoModal({ visible, permKey, onGrant, onSkip }) {
  const { t } = useTranslation();

  if (!permKey) return null;

  const legitimateItems = t(`${permKey}.legitimate`, { returnObjects: true });
  const maliciousItems = t(`${permKey}.malicious`, { returnObjects: true });

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      statusBarTranslucent
      onRequestClose={onSkip}
    >
      <View style={styles.overlay}>
        <View style={styles.sheet}>
          <View style={styles.handle} />

          <Text style={styles.title}>{t(`${permKey}.infoTitle`)}</Text>
          <Text style={styles.subtitle}>{t(`${permKey}.infoSubtitle`)}</Text>

          <ScrollView style={styles.scroll} showsVerticalScrollIndicator={false}>
            {/* Legitimate column */}
            <View style={styles.card}>
              <View style={[styles.cardHeader, { backgroundColor: calcColors.legitimate }]}>
                <Text style={styles.cardHeaderIcon}>✅</Text>
                <Text style={[styles.cardHeaderText, { color: '#a5d6a7' }]}>
                  {t(`${permKey}.legitimateTitle`)}
                </Text>
              </View>
              {legitimateItems.map((item, i) => (
                <View key={i} style={styles.listRow}>
                  <Text style={[styles.bullet, { color: '#a5d6a7' }]}>•</Text>
                  <Text style={styles.listText}>{item}</Text>
                </View>
              ))}
            </View>

            {/* Malicious column */}
            <View style={[styles.card, { marginTop: 10 }]}>
              <View style={[styles.cardHeader, { backgroundColor: calcColors.malicious }]}>
                <Text style={styles.cardHeaderIcon}>⚠️</Text>
                <Text style={[styles.cardHeaderText, { color: '#ef9a9a' }]}>
                  {t(`${permKey}.maliciousTitle`)}
                </Text>
              </View>
              {maliciousItems.map((item, i) => (
                <View key={i} style={styles.listRow}>
                  <Text style={[styles.bullet, { color: '#ef9a9a' }]}>•</Text>
                  <Text style={styles.listText}>{item}</Text>
                </View>
              ))}
            </View>
          </ScrollView>

          <TouchableOpacity style={styles.grantBtn} onPress={onGrant}>
            <Text style={styles.grantText}>{t('common.grant')}</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.skipBtn} onPress={onSkip}>
            <Text style={styles.skipText}>{t('common.skip')}</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.7)',
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: '#1e1e2e',
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    paddingHorizontal: 20,
    paddingBottom: 32,
    maxHeight: '85%',
  },
  handle: {
    width: 40,
    height: 4,
    backgroundColor: '#3a3a52',
    borderRadius: 2,
    alignSelf: 'center',
    marginTop: 12,
    marginBottom: 20,
  },
  title: {
    fontSize: 22,
    fontWeight: '700',
    color: '#e8e8f0',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: '#9090b0',
    marginBottom: 16,
  },
  scroll: {
    maxHeight: 340,
  },
  card: {
    borderRadius: 12,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.05)',
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
    gap: 8,
  },
  cardHeaderIcon: {
    fontSize: 16,
  },
  cardHeaderText: {
    fontSize: 13,
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  listRow: {
    flexDirection: 'row',
    paddingHorizontal: 14,
    paddingVertical: 8,
    gap: 8,
    borderTopWidth: 1,
    borderTopColor: 'rgba(255,255,255,0.04)',
  },
  bullet: {
    fontSize: 16,
    lineHeight: 20,
  },
  listText: {
    flex: 1,
    fontSize: 14,
    color: '#c0c0d8',
    lineHeight: 20,
  },
  grantBtn: {
    marginTop: 20,
    backgroundColor: '#6a1b9a',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
  },
  grantText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '700',
  },
  skipBtn: {
    marginTop: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  skipText: {
    color: '#7070a0',
    fontSize: 14,
  },
});
