import { render, screen, fireEvent } from '@testing-library/react';
import App from './App';
import { describe, it, expect, vi } from 'vitest';

// Mock scrollIntoView since it's not implemented in jsdom
window.HTMLElement.prototype.scrollIntoView = vi.fn();

describe('App Component', () => {
  it('renders without crashing and shows initial elements', () => {
    render(<App />);
    expect(screen.getByText('LuxBank AI')).toBeInTheDocument();
    expect(screen.getByText('Hello! I am your personal banking assistant. How can I help you today?')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Ask about loans, accounts, or services...')).toBeInTheDocument();
  });

  it('allows typing in the chat input', () => {
    render(<App />);
    const input = screen.getByPlaceholderText('Ask about loans, accounts, or services...');
    fireEvent.change(input, { target: { value: 'What is my balance?' } });
    expect(input).toHaveValue('What is my balance?');
  });

  it('renders a user message when sending', () => {
    render(<App />);
    const input = screen.getByPlaceholderText('Ask about loans, accounts, or services...');
    
    // Type in the input
    fireEvent.change(input, { target: { value: 'How to apply for a loan?' } });
    
    // Find and click the send button
    // The button has a Send icon, we can find it by finding its container. Since we don't have aria-labels, we'll find by class name
    const sendButton = document.querySelector('.btn-primary');
    if (sendButton) {
        fireEvent.click(sendButton);
    }

    expect(screen.getByText('How to apply for a loan?')).toBeInTheDocument();
  });
});
