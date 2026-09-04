import * as MediaLibrary from 'expo-media-library';

export async function runStorageDemo() {
  const { assets, totalCount } = await MediaLibrary.getAssetsAsync({
    first: 30,
    sortBy: [[MediaLibrary.SortBy.creationTime, false]],
    mediaType: [MediaLibrary.MediaType.photo, MediaLibrary.MediaType.video],
  });

  const items = assets.map(a => ({
    filename: a.filename,
    mediaType: a.mediaType,
    creationTime: a.creationTime,
  }));

  return { items, count: totalCount, templateVars: { count: totalCount } };
}
