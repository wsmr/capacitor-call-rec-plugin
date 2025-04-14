import { CallRecorderPlugin } from 'capacitor-call-rec-plugin';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    CallRecorderPlugin.echo({ value: inputValue })
}
