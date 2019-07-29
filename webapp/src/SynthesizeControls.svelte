<script>
  import Icon from "fa-svelte";
  import { faVolumeUp } from "@fortawesome/free-solid-svg-icons";

  export let textToTranslate = "";

  let languages = [
    { id: 1, text: 'Español' },
    { id: 2, text: `de` },
    { id: 3, text: `dgd` }
  ];

  let selectedLanguage;

  const synthesize = () => {
    console.log(
      "SYNTHESIZE CLICKED with LANG=${" +
        selectedLanguage.text +
        "} AND TEXT=${" +
        textToTranslate +
        "}"
    );
  };
</script>

<style>
  .synthesize-controls {
    float: left;
  }

  .synthesize-button {
    display: inline-block;
    vertical-align: bottom;
    font-size: 2rem;
    text-align: center;
    height: 45px;
  }

  .synthesize-button:hover {
    color: #555;
  }
</style>

<div class="synthesize-controls">
  <select bind:value={selectedLanguage}>
    {#each languages as language}
      <option value={language}>
        {@html language.text}
      </option>
    {/each}
  </select>
  {#if textToTranslate}
    <div class="synthesize-button" on:click={synthesize}>
      <Icon icon={faVolumeUp} />
    </div>
  {/if}
</div>
