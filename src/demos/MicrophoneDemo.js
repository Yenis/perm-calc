import React, { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, Modal } from 'react-native';
import { Audio } from 'expo-av';
import * as FileSystem from 'expo-file-system';
import * as MediaLibrary from 'expo-media-library';

const MAX_SECONDS = 8; // auto-stop after a short clip — no user input needed

export function MicrophoneDemo({ visible, onComplete, onError, t }) {
  const [seconds, setSeconds] = useState(0);
  const [phase, setPhase] = useState('recording'); // 'recording' | 'saving'
  const recordingRef = useRef(null);
  const timerRef = useRef(null);

  useEffect(() => {
    if (visible) {
      startRecording();
    } else {
      cleanup();
    }
    return cleanup;
  }, [visible]);

  useEffect(() => {
    if (seconds >= MAX_SECONDS && phase === 'recording') {
      stopAndSave();
    }
  }, [seconds]);

  const startRecording = async () => {
    try {
      await Audio.setAudioModeAsync({
        allowsRecordingIOS: true,
        playsInSilentModeIOS: true,
      });
      const { recording } = await Audio.Recording.createAsync(
        Audio.RecordingOptionsPresets.HIGH_QUALITY
      );
      recordingRef.current = recording;
      setSeconds(0);
      setPhase('recording');

      timerRef.current = setInterval(() => {
        setSeconds(s => s + 1);
      }, 1000);
    } catch (err) {
      onError(err);
    }
  };

  const stopAndSave = async () => {
    if (phase !== 'recording') return;
    setPhase('saving');

    clearInterval(timerRef.current);
    const rec = recordingRef.current;
    if (!rec) return;

    try {
      await rec.stopAndUnloadAsync();
      recordingRef.current = null;
      const uri = rec.getURI();
      const elapsed = formatTime(seconds);

      // Copy to a stable, human-readable path
      const destPath = `${FileSystem.documentDirectory}permcalc_demo.m4a`;
      await FileSystem.copyAsync({ from: uri, to: destPath });

      // Try to save to MediaLibrary so it appears in Files app
      let savedUri = destPath;
      try {
        const { status } = await MediaLibrary.requestPermissionsAsync();
        if (status === 'granted') {
          await MediaLibrary.saveToLibraryAsync(destPath);
        }
      } catch (_) {}

      onComplete({ fileUri: savedUri, duration: elapsed });
    } catch (err) {
      onError(err);
    }
  };

  const cleanup = () => {
    clearInterval(timerRef.current);
    if (recordingRef.current) {
      recordingRef.current.stopAndUnloadAsync().catch(() => {});
      recordingRef.current = null;
    }
    setSeconds(0);
    setPhase('recording');
  };

  const formatTime = (s) => {
    const m = Math.floor(s / 60);
    const sec = s % 60;
    return `${m}:${String(sec).padStart(2, '0')}`;
  };

  const progress = Math.min(seconds / MAX_SECONDS, 1);

  if (!visible) return null;

  return (
    <Modal visible={visible} transparent animationType="fade" statusBarTranslucent>
      <View style={styles.overlay}>
        <View style={styles.box}>
          {phase === 'recording' ? (
            <>
              <View style={styles.micIcon}>
                <Text style={styles.micEmoji}>🎙️</Text>
                <View style={[styles.pulse, { opacity: 0.3 + (seconds % 2) * 0.4 }]} />
              </View>

              <Text style={styles.timer}>{formatTime(seconds)}</Text>
              <Text style={styles.maxLabel}>/ {formatTime(MAX_SECONDS)}</Text>

              {/* Progress bar */}
              <View style={styles.progressTrack}>
                <View style={[styles.progressFill, { width: `${progress * 100}%` }]} />
              </View>

              <Text style={styles.hint}>{t('microphone.demoRecording')}</Text>
            </>
          ) : (
            <>
              <Text style={styles.savingText}>{t('microphone.demoSaving')}</Text>
            </>
          )}
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.9)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  box: {
    backgroundColor: '#1e1e2e',
    borderRadius: 20,
    width: '100%',
    padding: 28,
    alignItems: 'center',
    gap: 14,
  },
  micIcon: {
    position: 'relative',
    width: 80,
    height: 80,
    alignItems: 'center',
    justifyContent: 'center',
  },
  micEmoji: {
    fontSize: 44,
    zIndex: 2,
  },
  pulse: {
    position: 'absolute',
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#ef5350',
    zIndex: 1,
  },
  timer: {
    fontSize: 52,
    fontWeight: '200',
    color: '#e8e8f0',
    letterSpacing: 2,
  },
  maxLabel: {
    fontSize: 14,
    color: '#7070a0',
    marginTop: -10,
  },
  progressTrack: {
    width: '100%',
    height: 4,
    backgroundColor: '#2a2a3e',
    borderRadius: 2,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#ef5350',
    borderRadius: 2,
  },
  hint: {
    color: '#ef9a9a',
    fontSize: 14,
  },
  savingText: {
    color: '#b0b0c8',
    fontSize: 15,
    padding: 20,
  },
});
