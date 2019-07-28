import App from './App.svelte';

const app = new App({
	target: document.body,
	props: {
		appName: 'Speakeasy.',
		subheaderText: "Simple voice synthesis for multiple languages"
	}
});

export default app;