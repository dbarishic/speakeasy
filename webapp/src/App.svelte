<script>
  import Title from "./Title.svelte";
  import TextInput from "./TextInput.svelte";

  export let header;
  export let subheader;
</script>

<Title {header} {subheader} />
<TextInput />

<link
  href="https://fonts.googleapis.com/css?family=Overpass:100"
  rel="stylesheet" />
