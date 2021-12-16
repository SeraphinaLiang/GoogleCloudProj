let firebaseConfig;
if (location.hostname === "localhost") {
    firebaseConfig = {
        apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
        projectId: "demo-distributed-systems-kul",
    }
} else {
    //  (level 2) replace with your own configuration
    firebaseConfig = {
        apiKey: "AIzaSyBoJwe5hQRRZOg9lXnWWdpkanS0s8KsjvU",
        authDomain: "true-bit-333719.firebaseapp.com",
        projectId: "true-bit-333719",
        storageBucket: "true-bit-333719.appspot.com",
        messagingSenderId: "351412770290",
        appId: "1:351412770290:web:95884795ddff3aabac476b",
        measurementId: "G-70FHN4WBFD"
    }
}
firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
if (location.hostname === "localhost") {
    auth.useEmulator("http://localhost:8082");
}
const ui = new firebaseui.auth.AuthUI(auth);

ui.start('#firebaseui-auth-container', {
    signInOptions: [
        firebase.auth.EmailAuthProvider.PROVIDER_ID
    ],
    callbacks: {
        signInSuccessWithAuthResult: function (authResult, redirectUrl) {
            auth.currentUser.getIdToken(true)
                .then(async (idToken) => {
                    await fetch("/authenticate", {
                        method: "POST",
                        body: idToken,
                        headers: {
                            "Content-Type": "plain/text"
                        }
                    });
                    location.assign("/");
                });
            return false;
        },
    },
});
