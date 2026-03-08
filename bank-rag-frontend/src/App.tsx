import { useState, useEffect, useRef } from 'react'
import { Send, Bot, User, Menu, ShieldCheck } from 'lucide-react'

// Types
interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}

function App() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: 'Hello! I am your personal banking assistant. How can I help you today?',
      timestamp: new Date()
    }
  ])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const endRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = () => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSend = async () => {
    if (!input.trim()) return

    const userMsg: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
      timestamp: new Date()
    }

    setMessages(prev => [...prev, userMsg])
    setInput('')
    setLoading(true)

    try {
      // Simulate/Call API
      const apiUrl = import.meta.env.VITE_API_URL || '';
      const response = await fetch(`${apiUrl}/api/chat/ask`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: userMsg.content, role: 'user' })
      })

      if (!response.ok) throw new Error('Network response was not ok')

      const data = await response.json()

      const botMsg: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: data.content,
        timestamp: new Date()
      }
      setMessages(prev => [...prev, botMsg])
    } catch (error) {
      console.error(error)
      // Fallback for demo if backend not running
      setTimeout(() => {
        const fallback: Message = {
          id: (Date.now() + 1).toString(),
          role: 'assistant',
          content: "I'm sorry, I cannot connect to the banking server at the moment. Please ensure the Spring Boot backend is running on port 8080.",
          timestamp: new Date()
        }
        setMessages(prev => [...prev, fallback])
      }, 1000)
    } finally {
      setLoading(false)
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  return (
    <div className="app-container">
      {/* Sidebar */}
      <div className="sidebar">
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '40px' }}>
          <div style={{ width: 32, height: 32, background: 'linear-gradient(135deg, #ffd700, #b8860b)', borderRadius: 8 }}></div>
          <h2 style={{ fontSize: '1.2rem', fontWeight: 700, margin: 0 }}>LuxBank AI</h2>
        </div>

        <nav style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <button className="btn-icon" style={{ justifyContent: 'flex-start', display: 'flex', gap: '10px', width: '100%', borderRadius: 8, padding: '10px' }}>
            <span style={{ color: 'white' }}>Chat</span>
          </button>
          <button className="btn-icon" style={{ justifyContent: 'flex-start', display: 'flex', gap: '10px', width: '100%', borderRadius: 8, padding: '10px' }}>
            <span style={{}}>History</span>
          </button>
          <button className="btn-icon" style={{ justifyContent: 'flex-start', display: 'flex', gap: '10px', width: '100%', borderRadius: 8, padding: '10px' }}>
            <span style={{}}>Settings</span>
          </button>
        </nav>

        <div style={{ marginTop: 'auto' }}>
          <div className="glass-panel" style={{ padding: '15px', borderRadius: '12px', fontSize: '0.8rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px', color: '#4ade80' }}>
              <ShieldCheck size={16} />
              <span>Secure Connection</span>
            </div>
            <p style={{ margin: 0, opacity: 0.7 }}>Your conversations are end-to-end encrypted.</p>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-content">
        <header className="header">
          <h3 style={{ margin: 0 }}>Assistant</h3>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button className="btn-icon"><Menu size={20} /></button>
          </div>
        </header>

        <div className="chat-area">
          {messages.map(msg => (
            <div key={msg.id} className={`message-row ${msg.role === 'user' ? 'user' : 'bot'}`}>
              <div className={`avatar ${msg.role === 'user' ? 'user' : 'bot'}`}>
                {msg.role === 'user' ? <User size={20} /> : <Bot size={20} />}
              </div>
              <div className="message-bubble">
                {msg.content}
              </div>
            </div>
          ))}
          {loading && (
            <div className="message-row bot">
              <div className="avatar bot"><Bot size={20} /></div>
              <div className="message-bubble">
                <span style={{ fontStyle: 'italic' }}>Processing...</span>
              </div>
            </div>
          )}
          <div ref={endRef} />
        </div>

        <div className="input-area">
          <div className="input-wrapper">
            <input
              className="chat-input"
              placeholder="Ask about loans, accounts, or services..."
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <button className="btn-primary" onClick={handleSend} disabled={loading} style={{ padding: '8px 16px', display: 'flex' }}>
              <Send size={18} />
            </button>
          </div>
          <div style={{ textAlign: 'center', marginTop: '10px', fontSize: '0.75rem', color: 'gray' }}>
            AI can make mistakes. Please verify important information.
          </div>
        </div>
      </div>
    </div>
  )
}

export default App
