<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { darkTheme, NConfigProvider, NGlobalStyle, NThemeEditor } from 'naive-ui'
</script>

<template>
  <n-config-provider :theme="darkTheme" :abstract="true">
    <n-theme-editor>
      <header>
        <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="32" height="32" />

        <div class="wrapper">
          <nav>
            <RouterLink to="/">Welcome</RouterLink>
            <RouterLink to="/expense">Expense</RouterLink>
            <RouterLink to="/test">Test</RouterLink>
          </nav>
        </div>
      </header>
      <RouterView />
    </n-theme-editor>
    <n-global-style />
  </n-config-provider>
</template>

<style scoped>
header {
  display: flex;
  place-items: center;
  padding-bottom: calc(var(--section-gap) / 2);
}

.logo {
  margin: 0 2rem 0 0;
}

nav {
  font-size: 1rem;
  text-align: center;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}


</style>
