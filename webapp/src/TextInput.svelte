<script>
  import { onMount } from "svelte";
  import Icon from "fa-svelte";
  import { faTimes, faVolumeUp } from "@fortawesome/free-solid-svg-icons";

  import SynthesizeControls from "./SynthesizeControls.svelte";

  let clearTextIcon = faTimes;
  let synthesizeSpeechIcon = faVolumeUp;
  let textToTranslate = "";
  let textarea;
  let selectedLanguage;

  onMount(() => {
    textarea.select();
  });

  const autoResizeTextArea = () => {
    textarea.style.height = "";
    textarea.style.height = textarea.scrollHeight + "px";
  };

  const clearText = () => {
    textarea.value = "";
    textToTranslate = "";
    autoResizeTextArea();
    textarea.select();
  };
</script>

<style>
  a {
    color: #676778;
    text-decoration: none;
    font-size: 2rem;
  }

  .container {
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    min-height: 50%;
  }

  .text-input-wrapper {
    width: 65%;
  }

  textarea {
    border: none;
    outline: none;

    box-shadow: none;
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

    resize: none; /*remove the resize handle on the bottom right*/

    font-size: 2rem;
    font-family: "Overpass";
    box-sizing: border-box;
    overflow: auto hidden;
    padding: 5px;
    width: 97%;
    min-height: 150px;
    display: block;
  }

  .input-container {
    position: relative;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  }
  .input-container:hover,
  .material-shadow {
    box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  }

  .clearText {
    position: absolute;
    top: 0px;
    right: 0px;
    color: #333;
    font-size: 1.5rem;
    padding-right: 0.5%;
  }

  .clearText:hover {
    color: #555;
  }

  .clearText .tooltiptext {
    font-size: 12px;
    visibility: hidden;
    width: 80px;
    background-color: black;
    color: #fff;
    text-align: center;
    border-radius: 6px;
    padding: 5px 0;
    margin-left: -40px;

    /* Position the tooltip */
    position: absolute;
    z-index: 1;
    bottom: 100%;
    left: 50%;
  }

  .tooltiptext:after {
    top: 100%;
    left: 50%;
    border: solid transparent;
    content: " ";
    height: 0;
    width: 0;
    position: absolute;
    pointer-events: none;
    border-color: rgba(0, 0, 0, 0);
    border-top-color: #000000;
    border-width: 5px;
    margin-left: -5px;
  }

  .clearText:hover .tooltiptext {
    visibility: visible;
  }

  /* 
  ##Device = Tablets, Ipads (portrait)
  ##Screen = B/w 768px to 1024px
*/

  @media (min-width: 768px) and (max-width: 1024px) {
    .text-input-wrapper {
      width: 100%;
      padding-left: 7px;
      padding-right: 7px;
    }

    .clearText {
      width: 5%;
    }
  }

  /* 
    ##Device = Low Resolution Tablets, Mobiles (Landscape)
    ##Screen = B/w 481px to 767px
  */
  @media (min-width: 320px) and (max-width: 480px) {
    .text-input-wrapper {
      width: 100%;
      padding-left: 7px;
      padding-right: 7px;
    }

    .clearText {
      width: 5%;
    }
  }

  /* 
  ##Device = Most of the Smartphones Mobiles (Portrait)
  ##Screen = B/w 320px to 479px
*/

  @media (min-width: 320px) and (max-width: 480px) {
    .text-input-wrapper {
      width: 100%;
      padding-left: 7px;
      padding-right: 7px;
    }

    .clearText {
      width: 10%;
    }

    .tooltiptext {
      display: none;
    }
  }
</style>

<div class="container">
  <div class="text-input-wrapper">
    <div class="input-container {textToTranslate ? 'material-shadow' : ''}">
      {#if textToTranslate}
        <div on:click={clearText} class="clearText">
          <Icon icon={clearTextIcon} />
          <span class="tooltiptext">Clear text</span>
        </div>
      {/if}
      <textarea
        id="mainTextArea"
        placeholder="Enter text here"
        bind:this={textarea}
        bind:value={textToTranslate}
        on:input={autoResizeTextArea} />
    </div>

    <SynthesizeControls {textToTranslate} />
  </div>
</div>

<link
  href="https://fonts.googleapis.com/css?family=Overpass:100"
  rel="stylesheet" />
