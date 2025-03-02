import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import selectOnClick from '@/directives/selectOnClick.ts'

const app = createApp(App)

app.use(router)
app.directive('select-on-click', selectOnClick)
app.mount('#app')
