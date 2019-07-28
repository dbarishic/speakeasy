<script>
  export let appName;
  export let subheaderText;
</script>

<style>
  /* pt-sans-700 - latin */
  @font-face {
    font-family: "PT Sans";
    font-style: normal;
    font-weight: 700;
    src: url("../fonts/pt-sans-v11-latin-700.eot"); /* IE9 Compat Modes */
    src: local("PT Sans Bold"), local("PTSans-Bold"),
      url("../fonts/pt-sans-v11-latin-700.eot?#iefix")
        format("embedded-opentype"),
      /* IE6-IE8 */ url("../fonts/pt-sans-v11-latin-700.woff2") format("woff2"),
      /* Super Modern Browsers */ url("../fonts/pt-sans-v11-latin-700.woff")
        format("woff"),
      /* Modern Browsers */ url("../fonts/pt-sans-v11-latin-700.ttf")
        format("truetype"),
      /* Safari, Android, iOS */
        url("../fonts/pt-sans-v11-latin-700.svg#PTSans") format("svg"); /* Legacy iOS */
  }

  h1 {
    color: black;
	font-family: 'PT Sans';
	font-size: 3rem;
    font-weight: 700;
	text-align: center;
	margin-bottom: 0;
  }

  p {
	  text-align: center;
	  margin-top: 3px;
  }
</style>

<h1>{appName}</h1>
<p>{subheaderText}</p>
