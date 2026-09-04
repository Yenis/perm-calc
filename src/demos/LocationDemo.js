import * as Location from 'expo-location';

export async function runLocationDemo() {
  const location = await Location.getCurrentPositionAsync({
    accuracy: Location.Accuracy.High,
  });

  const { latitude, longitude, accuracy } = location.coords;

  let address = null;
  try {
    const results = await Location.reverseGeocodeAsync({ latitude, longitude });
    if (results.length > 0) {
      const r = results[0];
      const parts = [r.streetNumber, r.street, r.city, r.region, r.country].filter(Boolean);
      address = parts.join(', ');
    }
  } catch (_) {}

  return { latitude, longitude, accuracy, address };
}
