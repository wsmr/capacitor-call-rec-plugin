export interface CallRecorderPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
