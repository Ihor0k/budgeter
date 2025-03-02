import type { DirectiveBinding, ObjectDirective, VNode } from 'vue'

function select(e: Event) {
  if (e.target instanceof HTMLInputElement) {
    e.target.select()
  }
}

const selectOnClick: ObjectDirective = {
  beforeMount(el: any, binding: DirectiveBinding<any, string, string>, vnode: VNode<any, any>, prevVNode: null): void {
    vnode.el.__vueParentComponent.props.onClick = select
  }
}

export default selectOnClick
