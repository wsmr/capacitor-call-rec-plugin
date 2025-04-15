import { registerPlugin } from '@capacitor/core';

import type { CallRecorderPlugin } from './definitions';

const CallRecorder = registerPlugin<CallRecorderPlugin>('CallRecorder', {
  web: () => import('./web').then((m) => new m.CallRecorderWeb()),
});

export * from './definitions';
export { CallRecorder };
