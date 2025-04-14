// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorCallRecPlugin",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorCallRecPlugin",
            targets: ["CallRecorderPluginPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "CallRecorderPluginPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CallRecorderPluginPlugin"),
        .testTarget(
            name: "CallRecorderPluginPluginTests",
            dependencies: ["CallRecorderPluginPlugin"],
            path: "ios/Tests/CallRecorderPluginPluginTests")
    ]
)