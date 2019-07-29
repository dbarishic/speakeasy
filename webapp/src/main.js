import App from './App.svelte';

const app = new App({
	target: document.body,
	props: {
		header: 'Speakeasy.',
		subheader: "Simple voice synthesis for multiple languages"
	}
});

export default app;