import React, { useState } from 'react';
import {
  View, Text, StyleSheet, SafeAreaView, StatusBar, TouchableOpacity, Alert,
} from 'react-native';
import { useTranslation } from 'react-i18next';
import i18n from '../i18n';

import { useCalculator } from '../hooks/useCalculator';
import { Display } from '../components/calculator/Display';
import { ButtonGrid } from '../components/calculator/ButtonGrid';
import { PermissionButton } from '../components/permissions/PermissionButton';
import { InfoModal } from '../components/permissions/InfoModal';
import { RevealModal } from '../components/permissions/RevealModal';
import { CameraDemo } from '../demos/CameraDemo';
import { MicrophoneDemo } from '../demos/MicrophoneDemo';
import { runContactsDemo } from '../demos/ContactsDemo';
import { runLocationDemo } from '../demos/LocationDemo';
import { runStorageDemo } from '../demos/StorageDemo';

import { Camera } from 'expo-camera';
import { Audio } from 'expo-av';
import * as Contacts from 'expo-contacts';
import * as Location from 'expo-location';
import * as MediaLibrary from 'expo-media-library';

const PERMS = ['camera', 'microphone', 'contacts', 'location', 'storage'];
const LANGS = ['en', 'de', 'bs'];

export function CalculatorScreen() {
  const { t } = useTranslation();
  const calc = useCalculator();

  // Which permission is being demoed right now
  const [activeKey, setActiveKey] = useState(null);
  // 'info' | 'demo' | 'reveal' | null
  const [step, setStep] = useState(null);
  // Data produced by the demo (photos, audio, contacts, etc.)
  const [demoData, setDemoData] = useState(null);

  // Camera-specific: show the CameraDemo modal during capture
  const [cameraDemoVisible, setCameraDemoVisible] = useState(false);
  // Microphone-specific
  const [micDemoVisible, setMicDemoVisible] = useState(false);

  const openInfoModal = (key) => {
    setActiveKey(key);
    setStep('info');
  };

  const handleGrant = async () => {
    setStep(null);
    switch (activeKey) {
      case 'camera':   await runCameraFlow();     break;
      case 'microphone': await runMicFlow();       break;
      case 'contacts': await runContactsFlow();   break;
      case 'location': await runLocationFlow();   break;
      case 'storage':  await runStorageFlow();    break;
    }
  };

  const handleSkip = () => {
    setActiveKey(null);
    setStep(null);
  };

  const handleRevealClose = () => {
    setActiveKey(null);
    setStep(null);
    setDemoData(null);
  };

  // ── Camera ──────────────────────────────────────────────────────────────
  const runCameraFlow = async () => {
    const { status } = await Camera.requestCameraPermissionsAsync();
    if (status !== 'granted') { showDenied(); return; }
    await MediaLibrary.requestPermissionsAsync();
    setCameraDemoVisible(true);
  };

  const handleCameraComplete = ({ frontUri, backUri }) => {
    setCameraDemoVisible(false);
    setDemoData({ frontUri, backUri });
    setStep('reveal');
  };

  const handleCameraError = (err) => {
    setCameraDemoVisible(false);
    Alert.alert('Camera error', String(err?.message ?? err));
  };

  // ── Microphone ──────────────────────────────────────────────────────────
  const runMicFlow = async () => {
    const { status } = await Audio.requestPermissionsAsync();
    if (status !== 'granted') { showDenied(); return; }
    setMicDemoVisible(true);
  };

  const handleMicComplete = (data) => {
    setMicDemoVisible(false);
    setDemoData(data);
    setStep('reveal');
  };

  const handleMicError = (err) => {
    setMicDemoVisible(false);
    Alert.alert('Microphone error', String(err?.message ?? err));
  };

  // ── Contacts ─────────────────────────────────────────────────────────────
  const runContactsFlow = async () => {
    const { status } = await Contacts.requestPermissionsAsync();
    if (status !== 'granted') { showDenied(); return; }
    try {
      const data = await runContactsDemo();
      setDemoData(data);
      setStep('reveal');
    } catch (err) {
      Alert.alert('Contacts error', String(err?.message ?? err));
    }
  };

  // ── Location ──────────────────────────────────────────────────────────────
  const runLocationFlow = async () => {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== 'granted') { showDenied(); return; }
    try {
      const data = await runLocationDemo();
      setDemoData(data);
      setStep('reveal');
    } catch (err) {
      Alert.alert('Location error', String(err?.message ?? err));
    }
  };

  // ── Storage ───────────────────────────────────────────────────────────────
  const runStorageFlow = async () => {
    const { status } = await MediaLibrary.requestPermissionsAsync();
    if (status !== 'granted') { showDenied(); return; }
    try {
      const data = await runStorageDemo();
      setDemoData(data);
      setStep('reveal');
    } catch (err) {
      Alert.alert('Storage error', String(err?.message ?? err));
    }
  };

  const showDenied = () => {
    Alert.alert(
      t('common.denied'),
      t('common.deniedMsg'),
      [{ text: t('common.close') }]
    );
    setActiveKey(null);
    setStep(null);
  };

  const cycleLang = () => {
    const idx = LANGS.indexOf(i18n.language);
    i18n.changeLanguage(LANGS[(idx + 1) % LANGS.length]);
  };

  return (
    <SafeAreaView style={styles.safe}>
      <StatusBar barStyle="light-content" backgroundColor="#12121f" />

      {/* Top bar */}
      <View style={styles.topBar}>
        <Text style={styles.appTitle}>PermCalc</Text>
        <TouchableOpacity style={styles.langBtn} onPress={cycleLang}>
          <Text style={styles.langText}>{i18n.language.toUpperCase()}</Text>
        </TouchableOpacity>
      </View>

      {/* Display */}
      <View style={styles.displayArea}>
        <Display value={calc.display} operator={calc.operator} />
      </View>

      {/* Buttons */}
      <ButtonGrid calc={calc} />

      {/* Divider with label */}
      <View style={styles.divider}>
        <View style={styles.dividerLine} />
        <Text style={styles.dividerLabel}>⚠ PERMISSION DEMOS</Text>
        <View style={styles.dividerLine} />
      </View>

      {/* Permission buttons row */}
      <View style={styles.permRow}>
        {PERMS.map(key => (
          <PermissionButton
            key={key}
            permKey={key}
            label={t(`permButtons.${key}`)}
            onPress={() => openInfoModal(key)}
          />
        ))}
      </View>

      {/* Modals */}
      <InfoModal
        visible={step === 'info'}
        permKey={activeKey}
        onGrant={handleGrant}
        onSkip={handleSkip}
      />

      <RevealModal
        visible={step === 'reveal'}
        permKey={activeKey}
        data={demoData}
        onClose={handleRevealClose}
      />

      {/* Camera active demo */}
      <CameraDemo
        visible={cameraDemoVisible}
        onComplete={handleCameraComplete}
        onError={handleCameraError}
        t={t}
      />

      {/* Microphone active demo */}
      <MicrophoneDemo
        visible={micDemoVisible}
        onComplete={handleMicComplete}
        onError={handleMicError}
        t={t}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: '#12121f',
  },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingTop: 8,
    paddingBottom: 4,
  },
  appTitle: {
    fontSize: 14,
    fontWeight: '700',
    color: '#4fc3f7',
    letterSpacing: 2,
    textTransform: 'uppercase',
  },
  langBtn: {
    backgroundColor: '#2a2a3e',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderWidth: 1,
    borderColor: '#3a3a52',
  },
  langText: {
    color: '#9090b0',
    fontSize: 12,
    fontWeight: '700',
    letterSpacing: 1,
  },
  displayArea: {
    flex: 1,
    minHeight: 120,
    maxHeight: 180,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 12,
    paddingVertical: 8,
    gap: 8,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: '#3a1a3a',
  },
  dividerLabel: {
    fontSize: 9,
    color: '#7a3a7a',
    fontWeight: '700',
    letterSpacing: 1.5,
  },
  permRow: {
    flexDirection: 'row',
    paddingHorizontal: 8,
    paddingBottom: 12,
  },
});
