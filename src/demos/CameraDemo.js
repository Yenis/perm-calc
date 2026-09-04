import React, { useRef, useState, useEffect } from 'react';
import { View, Text, StyleSheet, Modal } from 'react-native';
import { CameraView } from 'expo-camera';
import * as MediaLibrary from 'expo-media-library';

/**
 * CameraDemo mounts a hidden CameraView, captures front then rear photo,
 * saves both to gallery, and calls onComplete({ frontUri, backUri }).
 *
 * Capture fires automatically as soon as the camera is available — no tap
 * required. `onCameraReady` is the primary trigger, with a fallback timer in
 * case that event never fires (it can be unreliable on some Android devices).
 */
export function CameraDemo({ visible, onComplete, onError, t }) {
  const cameraRef = useRef(null);
  const [facing, setFacing] = useState('front');
  const captureStateRef = useRef('idle'); // 'idle' | 'front' | 'switching' | 'back' | 'done'
  const frontUriRef = useRef(null);

  useEffect(() => {
    if (!visible) {
      captureStateRef.current = 'idle';
      frontUriRef.current = null;
      setFacing('front');
    }
  }, [visible]);

  // Capture fires from onCameraReady the instant the (front, then back) camera
  // is actually ready — no fixed wait. This timer is only a failsafe so the
  // demo never hangs on the rare device where onCameraReady doesn't fire.
  useEffect(() => {
    if (!visible) return;
    const id = setTimeout(() => { triggerCapture(); }, 5000);
    return () => clearTimeout(id);
  }, [visible, facing]);

  const triggerCapture = async () => {
    if (facing === 'front' && captureStateRef.current === 'idle') {
      captureStateRef.current = 'front';
      await capturePhoto('front');
    } else if (facing === 'back' && captureStateRef.current === 'switching') {
      captureStateRef.current = 'back';
      await capturePhoto('back');
    }
  };

  const capturePhoto = async (side) => {
    try {
      const photo = await cameraRef.current.takePictureAsync({
        quality: 0.7,
        skipProcessing: false,
      });

      if (side === 'front') {
        frontUriRef.current = photo.uri;
        captureStateRef.current = 'switching';
        setFacing('back');
      } else {
        const backUri = photo.uri;
        const frontUri = frontUriRef.current;

        // Save both to gallery
        const { status } = await MediaLibrary.requestPermissionsAsync();
        if (status === 'granted') {
          await MediaLibrary.saveToLibraryAsync(frontUri);
          await MediaLibrary.saveToLibraryAsync(backUri);
        }

        captureStateRef.current = 'done';
        onComplete({ frontUri, backUri });
      }
    } catch (err) {
      onError(err);
    }
  };

  if (!visible) return null;

  return (
    <Modal visible={visible} transparent animationType="fade" statusBarTranslucent>
      <View style={styles.overlay}>
        <View style={styles.box}>
          <Text style={styles.statusText}>
            {facing === 'front' ? t('camera.demoCapturingFront') : t('camera.demoCapturingBack')}
          </Text>
          {/* Hidden camera view — mounted so we can capture */}
          <View style={styles.cameraWrapper}>
            <CameraView
              ref={cameraRef}
              style={styles.camera}
              facing={facing}
              onCameraReady={triggerCapture}
            />
            <View style={styles.scanLine} />
          </View>
          <Text style={styles.hint}>
            {facing === 'front' ? '📸 Front camera' : '📸 Rear camera'}
          </Text>
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
    padding: 20,
    alignItems: 'center',
    gap: 14,
  },
  statusText: {
    color: '#b0b0c8',
    fontSize: 15,
    textAlign: 'center',
  },
  cameraWrapper: {
    width: '100%',
    aspectRatio: 1,
    borderRadius: 16,
    overflow: 'hidden',
    backgroundColor: '#000',
  },
  camera: {
    flex: 1,
  },
  scanLine: {
    position: 'absolute',
    left: 0,
    right: 0,
    height: 2,
    backgroundColor: 'rgba(79,195,247,0.6)',
    top: '50%',
  },
  hint: {
    color: '#4fc3f7',
    fontSize: 13,
  },
});
