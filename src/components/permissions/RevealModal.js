import React from 'react';
import {
  View, Text, ScrollView, StyleSheet, TouchableOpacity, Modal,
  Image, FlatList,
} from 'react-native';
import { useTranslation } from 'react-i18next';

export function RevealModal({ visible, permKey, data, onClose }) {
  const { t } = useTranslation();

  if (!permKey || !data) return null;

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      statusBarTranslucent
      onRequestClose={onClose}
    >
      <View style={styles.overlay}>
        <View style={styles.container}>
          <View style={styles.topBar}>
            <Text style={styles.doneLabel}>{t(`${permKey}.revealTitle`, data.templateVars ?? {})}</Text>
          </View>

          <ScrollView style={styles.scroll} showsVerticalScrollIndicator={false}>
            <RevealContent permKey={permKey} data={data} t={t} />

            {/* Warning Banner */}
            <View style={styles.warningBox}>
              <Text style={styles.warningIcon}>⚠️</Text>
              <Text style={styles.warningText}>{t(`${permKey}.warning`)}</Text>
            </View>
          </ScrollView>

          <TouchableOpacity style={styles.closeBtn} onPress={onClose}>
            <Text style={styles.closeText}>{t('common.close')}</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
}

function RevealContent({ permKey, data, t }) {
  switch (permKey) {
    case 'camera':
      return <CameraReveal data={data} t={t} />;
    case 'microphone':
      return <MicReveal data={data} t={t} />;
    case 'contacts':
      return <ContactsReveal data={data} t={t} />;
    case 'location':
      return <LocationReveal data={data} t={t} />;
    case 'storage':
      return <StorageReveal data={data} t={t} />;
    default:
      return null;
  }
}

function CameraReveal({ data, t }) {
  return (
    <View>
      <Text style={styles.revealDesc}>{t('camera.revealDesc')}</Text>
      <View style={styles.photoRow}>
        {data.frontUri && (
          <View style={styles.photoCard}>
            <Image source={{ uri: data.frontUri }} style={styles.photo} />
            <Text style={styles.photoLabel}>{t('camera.revealFront')}</Text>
          </View>
        )}
        {data.backUri && (
          <View style={styles.photoCard}>
            <Image source={{ uri: data.backUri }} style={styles.photo} />
            <Text style={styles.photoLabel}>{t('camera.revealBack')}</Text>
          </View>
        )}
      </View>
    </View>
  );
}

function MicReveal({ data, t }) {
  const [playing, setPlaying] = React.useState(false);
  const soundRef = React.useRef(null);

  const togglePlay = async () => {
    try {
      const { Audio } = require('expo-av');
      if (playing && soundRef.current) {
        await soundRef.current.stopAsync();
        await soundRef.current.unloadAsync();
        soundRef.current = null;
        setPlaying(false);
      } else {
        const { sound } = await Audio.Sound.createAsync({ uri: data.fileUri }, { shouldPlay: true });
        soundRef.current = sound;
        setPlaying(true);
        sound.setOnPlaybackStatusUpdate((status) => {
          if (status.didJustFinish) {
            setPlaying(false);
            soundRef.current = null;
          }
        });
      }
    } catch (_) {}
  };

  React.useEffect(() => {
    return () => {
      if (soundRef.current) {
        soundRef.current.unloadAsync();
      }
    };
  }, []);

  return (
    <View>
      <Text style={styles.revealDesc}>
        {t('microphone.revealDesc', { duration: data.duration ?? '0:00' })}
      </Text>
      <TouchableOpacity style={styles.playBtn} onPress={togglePlay}>
        <Text style={styles.playIcon}>{playing ? '⏹' : '▶'}</Text>
        <Text style={styles.playText}>{playing ? 'Stop' : 'Play Recording'}</Text>
      </TouchableOpacity>
      <Text style={styles.fileLabel}>{t('microphone.revealFile')}</Text>
    </View>
  );
}

function ContactsReveal({ data, t }) {
  const contacts = data.contacts ?? [];
  return (
    <View>
      <Text style={styles.revealDesc}>
        {t('contacts.revealDesc', { count: contacts.length })}
      </Text>
      {contacts.length === 0 ? (
        <Text style={styles.emptyText}>{t('contacts.revealNoContacts')}</Text>
      ) : (
        <View style={styles.listBox}>
          {contacts.slice(0, 50).map((c, i) => (
            <View key={i} style={[styles.contactRow, i > 0 && styles.borderTop]}>
              <View style={styles.avatar}>
                <Text style={styles.avatarText}>{(c.name ?? '?')[0].toUpperCase()}</Text>
              </View>
              <View style={styles.contactInfo}>
                <Text style={styles.contactName}>{c.name ?? 'Unknown'}</Text>
                {c.phone ? <Text style={styles.contactSub}>{c.phone}</Text> : null}
                {c.email ? <Text style={styles.contactSub}>{c.email}</Text> : null}
              </View>
            </View>
          ))}
          {contacts.length > 50 && (
            <Text style={styles.moreText}>...and {contacts.length - 50} more</Text>
          )}
        </View>
      )}
    </View>
  );
}

function LocationReveal({ data, t }) {
  return (
    <View>
      <Text style={styles.revealDesc}>{t('location.revealDesc')}</Text>
      <View style={styles.locationCard}>
        {data.address ? (
          <View style={styles.locationRow}>
            <Text style={styles.locationLabel}>{t('location.revealAddress')}</Text>
            <Text style={styles.locationValue}>{data.address}</Text>
          </View>
        ) : null}
        <View style={[styles.locationRow, { borderTopWidth: data.address ? 1 : 0, borderTopColor: 'rgba(255,255,255,0.06)' }]}>
          <Text style={styles.locationLabel}>{t('location.revealCoords')}</Text>
          <Text style={styles.locationValue}>
            {data.latitude?.toFixed(6)}, {data.longitude?.toFixed(6)}
          </Text>
        </View>
        {data.accuracy != null && (
          <View style={styles.locationRow}>
            <Text style={styles.locationValue}>
              {t('location.revealAccuracy', { meters: Math.round(data.accuracy) })}
            </Text>
          </View>
        )}
      </View>
    </View>
  );
}

function StorageReveal({ data, t }) {
  const items = data.items ?? [];
  return (
    <View>
      <Text style={styles.revealDesc}>
        {t('storage.revealDesc', { count: data.count ?? 0 })}
      </Text>
      {items.length === 0 ? (
        <Text style={styles.emptyText}>{t('storage.revealNoMedia')}</Text>
      ) : (
        <View style={styles.listBox}>
          <Text style={styles.sectionLabel}>{t('storage.revealRecentLabel')}</Text>
          {items.slice(0, 30).map((item, i) => (
            <View key={i} style={[styles.storageRow, i > 0 && styles.borderTop]}>
              <Text style={styles.fileIcon}>{item.mediaType === 'video' ? '🎥' : '🖼️'}</Text>
              <View style={styles.fileInfo}>
                <Text style={styles.fileName} numberOfLines={1}>{item.filename}</Text>
                {item.creationTime ? (
                  <Text style={styles.fileMeta}>{new Date(item.creationTime).toLocaleDateString()}</Text>
                ) : null}
              </View>
            </View>
          ))}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.85)',
    justifyContent: 'flex-end',
  },
  container: {
    backgroundColor: '#1e1e2e',
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    paddingHorizontal: 20,
    paddingBottom: 32,
    maxHeight: '92%',
  },
  topBar: {
    paddingTop: 16,
    paddingBottom: 12,
    borderBottomWidth: 1,
    borderBottomColor: 'rgba(255,255,255,0.06)',
  },
  doneLabel: {
    fontSize: 18,
    fontWeight: '700',
    color: '#e8e8f0',
    textAlign: 'center',
  },
  scroll: {
    marginTop: 12,
  },
  revealDesc: {
    fontSize: 14,
    color: '#b0b0c8',
    lineHeight: 22,
    marginBottom: 16,
  },
  warningBox: {
    backgroundColor: '#1a0000',
    borderWidth: 1,
    borderColor: '#b71c1c',
    borderRadius: 12,
    padding: 16,
    marginTop: 20,
    marginBottom: 8,
    flexDirection: 'row',
    gap: 10,
  },
  warningIcon: {
    fontSize: 18,
    marginTop: 2,
  },
  warningText: {
    flex: 1,
    fontSize: 13,
    color: '#ef9a9a',
    lineHeight: 20,
  },
  closeBtn: {
    marginTop: 16,
    backgroundColor: '#2a2a3e',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
  },
  closeText: {
    color: '#e8e8f0',
    fontSize: 16,
    fontWeight: '600',
  },
  photoRow: {
    flexDirection: 'row',
    gap: 10,
    marginBottom: 8,
  },
  photoCard: {
    flex: 1,
    borderRadius: 12,
    overflow: 'hidden',
    backgroundColor: '#12121f',
  },
  photo: {
    width: '100%',
    aspectRatio: 0.75,
  },
  photoLabel: {
    textAlign: 'center',
    fontSize: 12,
    color: '#9090b0',
    padding: 8,
  },
  playBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    backgroundColor: '#2a2a3e',
    borderRadius: 12,
    padding: 16,
    marginBottom: 10,
  },
  playIcon: {
    fontSize: 22,
  },
  playText: {
    color: '#4fc3f7',
    fontSize: 15,
    fontWeight: '600',
  },
  fileLabel: {
    fontSize: 12,
    color: '#7070a0',
    fontFamily: 'monospace',
  },
  listBox: {
    borderRadius: 12,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.06)',
    backgroundColor: '#12121f',
  },
  borderTop: {
    borderTopWidth: 1,
    borderTopColor: 'rgba(255,255,255,0.05)',
  },
  contactRow: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
    gap: 12,
  },
  avatar: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: '#1a3a4a',
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarText: {
    color: '#4fc3f7',
    fontSize: 16,
    fontWeight: '700',
  },
  contactInfo: {
    flex: 1,
  },
  contactName: {
    color: '#e8e8f0',
    fontSize: 14,
    fontWeight: '500',
  },
  contactSub: {
    color: '#7070a0',
    fontSize: 12,
    marginTop: 1,
  },
  moreText: {
    color: '#7070a0',
    fontSize: 13,
    textAlign: 'center',
    padding: 12,
  },
  locationCard: {
    borderRadius: 12,
    backgroundColor: '#12121f',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.06)',
    overflow: 'hidden',
    marginBottom: 8,
  },
  locationRow: {
    padding: 14,
  },
  locationLabel: {
    fontSize: 11,
    color: '#7070a0',
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    marginBottom: 4,
  },
  locationValue: {
    fontSize: 15,
    color: '#e8e8f0',
    fontWeight: '500',
  },
  storageRow: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
    gap: 10,
  },
  fileIcon: {
    fontSize: 18,
  },
  fileInfo: {
    flex: 1,
  },
  fileName: {
    color: '#e8e8f0',
    fontSize: 13,
  },
  fileMeta: {
    color: '#7070a0',
    fontSize: 11,
    marginTop: 2,
  },
  emptyText: {
    color: '#7070a0',
    fontSize: 14,
    textAlign: 'center',
    padding: 20,
  },
  sectionLabel: {
    fontSize: 11,
    color: '#7070a0',
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    padding: 12,
    paddingBottom: 6,
  },
});
