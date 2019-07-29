<script>
	import Title from './Title.svelte';
	import TextInput from './TextInput.svelte';

	export let header;
	export let subheader;
</script>

<Title header={header} subheader={subheader}></Title>
<TextInput></TextInput>

