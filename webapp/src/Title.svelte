<script>
  import { fade, draw, fly } from "svelte/transition";

  export let header;
  export let subheader;

  let visible = false;

  document.addEventListener("DOMContentLoaded", () => {
    visible = true;
  });
</script>

<style>
  h1 {
    color: #676778;
    font-family: "Overpass";
    font-size: 3rem;
    font-weight: 200;
    text-align: center;
    margin-bottom: 0;
    letter-spacing: 0.12em;
  }

  p {
    color: #676778;
    font-family: "Overpass";
    text-align: center;
    margin-top: -3px;
  }
</style>

{#if visible}
  <h1 transition:fade>{header}</h1>
  <p transition:fade>
    {#each subheader as char, i}
      <span in:fade={{ delay: 100 + i * 20, duration: 1000 }}>{char}</span>
    {/each}
  </p>
{/if}

<link
  href="https://fonts.googleapis.com/css?family=Overpass:100"
  rel="stylesheet" />
