import Foundation

@objc public class CallRecorder: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
