import { CallRecorder } from 'capacitor-call-rec-plugin';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    CallRecorder.echo({ value: inputValue })
}
