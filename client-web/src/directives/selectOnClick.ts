import type { ObjectDirective } from 'vue'

function select(e: Event) {
  if (e.target instanceof HTMLInputElement) {
    e.target.select()
  }
}

const selectOnClick: ObjectDirective = {
  beforeMount(el, binding, vnode): void {
    vnode.el.__vueParentComponent.props.onClick = select
  }
}

export default selectOnClick
