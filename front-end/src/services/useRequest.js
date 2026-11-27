import { useCallback, useEffect, useState } from 'react'
import { errorMessage } from './api'

export function useRequest(load) {
  const [state, setState] = useState({ loading: true, data: null, error: '' })
  const [revision, setRevision] = useState(0)
  const reload = useCallback(() => setRevision(value => value + 1), [])

  useEffect(() => {
    let current = true
    setState(previous => ({ ...previous, loading: true, error: '' }))
    load().then(
      data => { if (current) setState({ loading: false, data, error: '' }) },
      error => { if (current) setState({ loading: false, data: null, error: errorMessage(error) }) },
    )
    return () => { current = false }
  }, [load, revision])

  return { ...state, reload }
}
